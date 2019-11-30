#include <jni.h>
#include <string>
#include <sys/ptrace.h>
#include<android/log.h>
#include <unistd.h>
#include <fcntl.h>
#include<sys/types.h>
#include<sys/wait.h>
#include<cstdlib>
#include<ctime>
#include <sys/inotify.h>
#include <pthread.h>
#include <filesystem>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <elf.h>
#include <sys/mman.h>

//#define DEBUG

#ifdef DEBUG
#define TAG "MACHINE_JNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__)
#else
#define LOGD(...)
#define LOGI(...)
#define LOGW(...)
#define LOGE(...)
#define LOGF(...)
#endif


const int STACK_LENGTH = 1000000;
long stack[STACK_LENGTH] = {0};
const int MAXN = 20;
unsigned long initArr[MAXN + 1] = {0};
#define JNIREG_CLASS "com/happy/machine/MainActivity"
#define MY_SEGMENT_NAME ".mytext"

JNICALL void
encrypt(unsigned long *v, unsigned long *key, unsigned int count) __attribute__((visibility("hidden")));//__attribute__((section (MY_SEGMENT_NAME)));

JNICALL jint checkIt(JNIEnv *env, jobject obj, jstring str)  __attribute__((visibility("hidden")));//__attribute__((section (MY_SEGMENT_NAME),visibility("hidden")));// __attribute__((visibility("hidden")));

long CheckPort23946ByTcp() __attribute__((visibility("hidden")));

void *anti_by_inotify(void *) __attribute__((visibility("hidden")));

void anti_by_time() __attribute__((visibility("hidden")));

long anti_debug()__attribute__((visibility("hidden")));

long is_frida_server_listening() __attribute__((visibility("hidden"),section (MY_SEGMENT_NAME)));


long SearchObjProcess() __attribute__((visibility("hidden")));


__attribute__((visibility("hidden")))
long ackman(long m, long n) {
    int pos = 1;
    stack[0] = m;
    stack[1] = n;
    while (pos) {
        n = stack[pos--];
        m = stack[pos];
        if (m == 0)
            stack[pos] = n + 1;
        if (m != 0 && n == 0) {
            stack[pos++] = m - 1;
            stack[pos] = 1;
        }
        if (m != 0 && n != 0) {
            stack[pos++] = m - 1;
            stack[pos++] = m;
            stack[pos] = n - 1;
        }
    }
    return stack[0];
}


extern "C" JNIEXPORT jlong JNICALL
Java_com_happy_machine_MainActivity_IdleOnce(
        JNIEnv *env,
        jobject /* this */, jint m, jint n) {
    if (n > MAXN || n == 0) {
        return -1;
    }
    long a = ackman(m, n);
    initArr[n] = a;
    usleep(200 * 1000);
    return a + random();
}

__attribute__((visibility("hidden")))
jint addint1(JNIEnv *env, jobject obj, jint a, jint b) {
    return a + b;
}

static JNINativeMethod gMethods[] = {
        {"addint",     "(II)I",                                  (void *) addint1},
        {"verify",     "(Ljava/lang/String;)I",                  (void *) checkIt}

};

static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == nullptr) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

static int registerNatives(JNIEnv *env) {
    if (!registerNativeMethods(env, JNIREG_CLASS, gMethods,
                               sizeof(gMethods) / sizeof(gMethods[0])))
        return JNI_FALSE;

    return JNI_TRUE;
}


long anti_debug() {
    long iRet = ptrace(PTRACE_TRACEME, 0, 0, 0);
    ptrace(PTRACE_DETACH, iRet, 0, 0);
    if (-1 == iRet) {
        LOGI("ptrace fail. DEBUG DETECTED!!\n");
        return random() + 11;
    } else {
        LOGI("ptrace ret:%ld\n", iRet);
        return 0;
    }
}

//__attribute__((section (MY_SEGMENT_NAME)))
JNICALL void encrypt(unsigned long *v, unsigned long *key, unsigned int count) {
    unsigned long l = v[0], r = v[1], sum = 0, delta = 0x9e3779b9;

    //count: 32 or 64
    for (size_t i = 0; i < count; i++) {
        l += (((r << 4) ^ (r >> 5)) + r) ^ (sum + key[sum & 3]);
        sum += delta;
        r += (((l << 4) ^ (l >> 5)) + l) ^ (sum + key[(sum >> 11) & 3]);
    }

    v[0] = l;
    v[1] = r;
}

__attribute__((visibility("hidden")))
//__attribute__((section (MY_SEGMENT_NAME),visibility("hidden")))
JNICALL jint checkIt(JNIEnv *env, jobject obj, jstring str) {
    const int LEN_ERROR = 2;
    const int PREFIX_ERROR = 3;
    const int FIRST_ERROR = 4;
    const int SECOND_ERROR = 5;
    const char *strAry = env->GetStringUTFChars(str, nullptr);
    if (strAry == nullptr) {
        return -1;
    }
    size_t len = strlen(strAry);
    LOGI("length: %d,%d", len, strlen("d3ctf{15eLLaf9481a5I5f}"));//fake
    LOGI("INPUT: %s", strAry);
    if (len != 23) {
        return LEN_ERROR;
    }
    int total = 0;
    char *c = "d3ctf{";
    for (int i = 0; i < 6; i++) {
        if (strAry[i] != c[i])return PREFIX_ERROR;
    }
    if (strAry[22] != '}')return PREFIX_ERROR;

    /**
     * First Step
     * */
    unsigned long v[2] = {0, 0};
    int startPos = 6;
    for (int i = startPos + 0; i < startPos + 8; i++) {
        ((char *) v)[i - startPos] = strAry[i];
    }
    unsigned long *key = &initArr[8];
    LOGI("[+] KEY IS %ld %ld %ld %ld", key[0], key[1], key[2], key[3]);
    LOGI("[+] v BEFORE ENCODE IS %ld %ld", v[0], v[1]);
    encrypt(v, key, 32);
    LOGI("[+] v AFTER ENCODE IS %ld %ld", v[0], v[1]);
    if (v[0] != 246553640 || v[1] != 2138606322) {
        return FIRST_ERROR;
    }
    /**
     * Second Step
     * */
    startPos = 14;
    for (int i = startPos + 0; i < startPos + 8; i++) {
        ((char *) v)[i - startPos] = strAry[i];
    }
    key[2] = ackman(3,16);//524285;
    key[3] = 262141;//ackman(3,15);
    key[0] = initArr[0];
    LOGI("[+][+] KEY IS %ld %ld %ld %ld", key[0], key[1], key[2], key[3]);
    LOGI("[+][+] v BEFORE ENCODE IS %ld %ld", v[0], v[1]);
    encrypt(v, key, 64);
    LOGI("[+][+] v AFTER ENCODE IS %ld %ld", v[0], v[1]);
    if (v[0] != -1227780298 || v[1] != -783437692) {
        return SECOND_ERROR;
    }
    return 1;
    /*
     *  INPUT: d3ctf{BypA55_W3lL-D0ne}
        10-10 19:21:33.025 5318-5318/? I/MACHINE_JNI: [+] KEY IS 2045 4093 8189 16381
        10-10 19:21:33.025 5318-5318/? I/MACHINE_JNI: [+] v BEFORE ENCODE IS 1097890114 1465857333
        10-10 19:21:33.025 5318-5318/? I/MACHINE_JNI: [+] v AFTER ENCODE IS 246553640 2138606322
        10-10 19:21:33.025 5318-5318/? I/MACHINE_JNI: [+][+] KEY IS 1107 4093 524285 262141
        10-10 19:21:33.025 5318-5318/? I/MACHINE_JNI: [+][+] v BEFORE ENCODE IS 759983155 1701720132
        10-10 19:21:33.025 5318-5318/? I/MACHINE_JNI: [+][+] v AFTER ENCODE IS -1227780298 -783437692
     *
     * */
}


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    pthread_t thread;
    pthread_create(&thread, NULL, anti_by_inotify, NULL);
    //anti_by_time();
    srand(time(nullptr));
    // anti_debug Should be the last since pclose will stuck after ptrace
    unsigned long sig = CheckPort23946ByTcp() ^SearchObjProcess() ^anti_debug() + is_frida_server_listening();
    initArr[0] = sig;
    if (sig != 1107) { // comment here, 1107 is a signature
        LOGE("DEBUG DETECTED!!");
        return -1;
    } else {
        LOGI("No Debugger");
    };

    JNIEnv *env;
    if (vm->GetEnv((void **) (&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    assert(env != nullptr);

    if (!registerNatives(env)) {
        return -1;
    }
    LOGE("Finish init\n");
    return JNI_VERSION_1_6;
}

void init_happy() __attribute__((constructor, visibility("hidden")));

unsigned long getLibAddr();


// here decode the So file segment
__attribute__((constructor, visibility("hidden")))
void init_happy() {

    unsigned int nblock; // target section size
    unsigned int num_page;//section page number
    unsigned long base;
    unsigned long text_addr; // target section offset
    unsigned int i;
    Elf32_Ehdr *ehdr32;
    Elf64_Ehdr *ehdr64;

    base = getLibAddr(); // so base addr
    LOGI("Get the native-lib base addr 0x%x", base);
    ehdr32 = (Elf32_Ehdr *) base;
    int type = *((ehdr32->e_ident) + 4);
    LOGI("type =  %d", type);
    if (type == 1) {
        ehdr32 = (Elf32_Ehdr *) base;
        text_addr = ehdr32->e_shoff + base;
        nblock = ehdr32->e_entry;
    } else if (type == 2) {
        ehdr64 = (Elf64_Ehdr *) base;
        text_addr = ehdr64->e_shoff + base;
        nblock = ehdr64->e_entry;
    }
    num_page =
            (text_addr % PAGE_SIZE + nblock) / PAGE_SIZE + ((text_addr % PAGE_SIZE + nblock) % PAGE_SIZE == 0 ? 0 : 1);

    LOGI("offset = 0x%x, e_entry = 0x%x, nblock =  0x%x, num_page = %d, base =  0x%x", ehdr32->e_shoff,
         ehdr32->e_entry, nblock, num_page, text_addr);

    if (mprotect((char *) (text_addr / PAGE_SIZE * PAGE_SIZE), PAGE_SIZE * num_page,
                 PROT_READ | PROT_EXEC | PROT_WRITE) != 0) {
        LOGI("mem privilege change failed");
    }
    LOGI("mprotect successfully");

    //decode here

    //TODO: modify text_addr addr
    for (i = 0; i < nblock; i++) {
        char *addr = (char *) (text_addr + i);
        *addr = ~(*addr);
    }
    for (i = 0; i < nblock; i++) {
        char *addr = (char *) (text_addr + i);
        *addr = ~(*addr);
    }
    LOGI("Finish decode , start at 0x%x", text_addr);
    if (mprotect((char *) (text_addr / PAGE_SIZE * PAGE_SIZE), PAGE_SIZE * num_page, PROT_READ | PROT_EXEC) != 0) {
        LOGI("mem privilege recover change failed");
    }
    LOGI("Decrypt success");
}

__attribute__((visibility("hidden")))
unsigned long getLibAddr() {
    unsigned long ret = 0;
    char name[] = "native-lib.so";
    char buf[4096];
    char *temp;
    int pid;
    FILE *fp;
    pid = getpid();
    sprintf(buf, "/proc/%d/maps", pid);
    fp = fopen(buf, "r");
    if (fp == NULL) {
        puts("open failed");
        goto _error;
    }
    while (fgets(buf, sizeof(buf), fp)) {
        if (strstr(buf, name)) {
            temp = strtok(buf, "-");
            ret = strtoul(temp, NULL, 16);
            break;
        }
    }
    _error:
    fclose(fp);
    return ret;
}

long CheckPort23946ByTcp() {
    const int bufsize = 1024;
    char filename[bufsize];
    char line[bufsize];
    int pid = getpid();
    FILE *fp;
    sprintf(filename, "proc/net/tcp");
    fp = fopen(filename, "r");//
    if (fp != NULL) {
        while (fgets(line, bufsize, fp)) {
            if (strncmp(line, "5D8A", 4) == 0) {
                int ret = kill(pid, SIGKILL);
                return random() + 12;
            }
        }
    }
    fclose(fp);
    return 0;
}

long SearchObjProcess() {
    FILE *pfile = nullptr;
    char buf[0x1000] = {0};

    //pfile=popen("ps | awk '{print $9}'","r"); // Some Mobile Don't support awk command
    pfile = popen("ps", "r");
    if (nullptr == pfile) {
        LOGI("SearchObjProcess popen fail!\n");
        return 0;
    }
    //LOGI("popen result:\n");
    while (fgets(buf, sizeof(buf), pfile)) {
        //LOGI("Traverse:%s\n", buf);
        char *strA = nullptr;
        char *strD = nullptr;
        char *strC = nullptr;
        char *strB = nullptr;
        strA = strstr(buf, "android_server");
        strB = strstr(buf, "gdbserver");
        strC = strstr(buf, "gdb");
        strD = strstr(buf, "fuwu");
        if (strA || strB || strC || strD) {
            // Detected Debugger
            LOGI("Find target Process\n");
            return random() + 11;
        } else {
            //LOGI("NOT FOUND\n");
        }
    }
    pclose(pfile);
    return 0;
}

void anti_by_time() {
    int pid = getpid();
    struct timeval t1{};
    struct timeval t2{};
    struct timezone tz{};
    gettimeofday(&t1, &tz);
    gettimeofday(&t2, &tz);
    int timeoff = static_cast<int>((t2.tv_sec) - (t1.tv_sec));
    if (timeoff > 1) {
        int ret = kill(pid, SIGKILL);
        return;
    }
}

void *anti_by_inotify(void *) {
    const int MAXLEN = 2048;
    int ppid = getpid();
    char buf[1024], readbuf[MAXLEN];
    int pid, wd, ret, len, i;
    int fd;
    fd_set readfds;
    ptrace(PTRACE_TRACEME, 0, 0, 0);
    fd = inotify_init1(IN_CLOEXEC);
    sprintf(buf, "/proc/%d/maps", ppid);

    wd = inotify_add_watch(fd, buf, IN_ALL_EVENTS);
    if (wd < 0) {
        LOGD("can't watch %s", buf);
        return nullptr;
    }
    while (1) {
        i = 0;
        FD_ZERO(&readfds);
        FD_SET(fd, &readfds);
        //select : monitor multi file handlers
        ret = select(fd + 1, &readfds, 0, 0, 0);
        if (ret == -1)
            break;
        if (ret) {
            len = read(fd, readbuf, MAXLEN);
            while (i < len) {
                struct inotify_event *event = (struct inotify_event *) &readbuf[i];
                LOGD("event mask %d\n", (event->mask & IN_ACCESS) || (event->mask & IN_OPEN));
                // monitor access(read) and open event
                if ((event->mask & IN_ACCESS) || (event->mask & IN_OPEN)) {
                    LOGD("Detected!!! kill!!!!!\n");
                    // TODO : kill process
                    //int ret = kill(ppid, SIGKILL);
                    LOGD("ret = %d", ret);
                    return nullptr;
                }
                i += sizeof(struct inotify_event) + event->len;
            }
        }
    }
    inotify_rm_watch(fd, wd);
    close(fd);
    return nullptr;
}

long is_frida_server_listening() {
    struct sockaddr_in sa;
    memset(&sa, 0, sizeof(sa));
    sa.sin_family = AF_INET;
    sa.sin_port = htons(27047);
    const char *s = "127.0.0.1";
    inet_aton(s, &(sa.sin_addr));
    int sock = socket(AF_INET, SOCK_STREAM | SOCK_CLOEXEC, 0);
    if (connect(sock, (struct sockaddr *) &sa, sizeof sa) != -1) {
        /* Frida server detected. Do something… */
        return random() ^ 1107 + 1200;
    }
    return 1107;
}