const QUIZ_DATA = {
  "categories": [
    {
      "id": "java-basics",
      "label": "Java 基础",
      "icon": "☕",
      "description": "基础概念、String、集合框架",
      "source": "java.md",
      "questions": [
        {
          "id": "jb-001",
          "question": "JVM、JDK、JRE 三者有什么区别？字节码执行模型是怎样的？",
          "answer": "JVM（Java Virtual Machine）是运行字节码的虚拟机；JRE（Java Runtime Environment）包含 JVM + 核心类库，是运行环境；JDK（Java Development Kit）包含 JRE + 开发工具（javac、javadoc等）。Java 采用「编译+解释」混合模型：源码先编译为字节码（.class），再由 JVM 解释执行，热点代码通过 JIT（Just-In-Time）编译为本地机器码加速运行。AOT（Ahead-Of-Time）编译则在运行前完成编译。",
          "difficulty": 1
        },
        {
          "id": "jb-002",
          "question": "Java 8 种基本类型与对应的包装类是什么？自动装箱/拆箱与 Integer Cache 有什么需要注意的？",
          "answer": "8 种基本类型：byte(BYTE)、short(SHORT)、int(INTEGER)、long(LONG)、float(FLOAT)、double(DOUBLE)、char(CHARACTER)、boolean(BOOLEAN)。自动装箱是基本类型自动转包装类，拆箱反之。Integer Cache 缓存了 -128~127 的 Integer 对象，在此范围内装箱会复用缓存对象，== 比较返回 true；超出范围则创建新对象，== 返回 false。比较包装类值应始终使用 equals()。",
          "difficulty": 1
        },
        {
          "id": "jb-003",
          "question": "「==」和 equals() 有什么区别？hashCode() 与 equals() 之间有什么一致性约束？",
          "answer": "「==」比较基本类型时比较值，比较引用类型时比较内存地址（是否同一对象）。equals() 是 Object 类方法，默认行为与「==」相同，但 String、Integer 等类重写了 equals() 来比较内容值。一致性约束：两个对象 equals() 为 true，则 hashCode() 必须相同；hashCode() 相同，equals() 不一定为 true。重写 equals() 必须重写 hashCode()，否则在 HashMap/HashSet 中会出现逻辑错误。",
          "difficulty": 2
        },
        {
          "id": "jb-004",
          "question": "方法重载（Overload）和重写（Override）的区别？静态分派与动态分派是什么？",
          "answer": "重载：同名方法参数列表不同（类型、个数、顺序），发生在编译期（静态分派），根据声明类型确定方法版本。重写：子类重新定义父类方法，方法签名完全相同，发生在运行期（动态分派），根据实际对象类型确定方法版本。静态分派的典型：方法重载根据变量的声明类型选择；动态分派的典型：方法重写根据实际创建的对象类型调用。",
          "difficulty": 2
        },
        {
          "id": "jb-005",
          "question": "接口和抽象类的区别？Java 8 的 default 方法带来了什么影响？",
          "answer": "接口：只能声明抽象方法（Java 8 前），不能有实例变量，支持多实现。抽象类：可以有抽象方法和具体方法，可以有实例变量，只能单继承。Java 8 引入 default 方法后，接口也能有方法实现，模糊了两者边界。default 方法用于接口演进（向已有接口添加方法而不破坏实现类）。关键区别：抽象类表示「is-a」关系，有状态和模板方法；接口表示「can-do」能力，定义行为契约。Java 8 接口还引入了 static 方法。",
          "difficulty": 2
        },
        {
          "id": "jb-006",
          "question": "深拷贝和浅拷贝的区别？Java 中有哪些序列化方案？",
          "answer": "浅拷贝：只复制对象本身和基本类型字段，引用类型字段仍指向原对象（共享引用）。深拷贝：递归复制所有引用类型字段，生成完全独立的副本。Java 实现拷贝的方式：(1) 实现 Cloneable 接口 + 重写 clone()（默认浅拷贝，需手动深拷贝）；(2) 序列化/反序列化（ObjectOutputStream/ObjectInputStream），天然深拷贝但性能较差；(3) JSON 序列化（Jackson/Gson），灵活但需处理循环引用；(4) Apache Commons Lang 的 SerializationUtils。",
          "difficulty": 2
        },
        {
          "id": "jb-007",
          "question": "String 为什么是不可变的？这种设计有什么安全与性能优势？",
          "answer": "String 不可变性由 final byte[] 数组和不可变设计保证（没有修改数组内容的公开方法）。优势：(1) 安全：作为 HashMap 的 key 保证 hash 不变；防止敏感信息泄露（如密码）；线程安全无需同步。(2) 性能：支持字符串常量池（String Pool）复用实例，减少内存占用；编译期优化可以合并字符串常量。String、StringBuilder（线程不安全）、StringBuffer（线程安全）的选择：单线程用 StringBuilder，多线程用 StringBuffer 或局部变量用 StringBuilder。",
          "difficulty": 2
        },
        {
          "id": "jb-008",
          "question": "字符串常量池是什么？intern() 方法的作用？new String(\"abc\") 创建了几个对象？",
          "answer": "字符串常量池（String Pool）是 JVM 中的一块特殊内存区域，用于存储字符串字面量，实现字符串复用。intern() 方法：如果池中已存在该字符串，返回池中引用；否则将字符串加入池中并返回引用。new String(\"abc\") 创建了两个对象：一个是常量池中的 \"abc\"（如果池中不存在），另一个是堆中的 new String 对象。在 Java 7 之后，常量池移到堆中，避免永久代 OOM。",
          "difficulty": 2
        },
        {
          "id": "jb-009",
          "question": "ArrayList 和 LinkedList 的区别？各自的适用场景？",
          "answer": "ArrayList：基于动态数组实现，支持随机访问 O(1)，中间插入/删除需移动元素 O(n)，扩容为 1.5 倍。实现了 RandomAccess 标记接口。LinkedList：基于双向链表实现，随机访问 O(n)，头尾插入/删除 O(1)。内存开销更大（每个节点存储前后指针）。适用场景：ArrayList 适合读多写少、随机访问频繁的场景；LinkedList 适合频繁在头尾增删的场景（如队列、栈）。实际开发中 ArrayList 使用频率远高于 LinkedList，因为 CPU 缓存对连续数组更友好。",
          "difficulty": 1
        },
        {
          "id": "jb-010",
          "question": "HashMap 的底层数据结构是怎样的？为什么长度必须是 2 的幂次方？",
          "answer": "JDK 8 中 HashMap 底层是「数组 + 链表 + 红黑树」。数组每个位置称为桶（bucket），通过 hash(key) % length 定位。链表长度超过 8 且数组长度 ≥ 64 时，链表转为红黑树；退化条件是红黑树节点 ≤ 6。长度为 2 的幂次方的原因：(1) hash 定位用位运算 hash & (n-1) 代替取模，效率更高；(2) 2 的幂次方使得 hash 值的低位能均匀分布到数组中，减少碰撞。负载因子默认 0.75，超过时触发扩容为 2 倍。",
          "difficulty": 2
        },
        {
          "id": "jb-011",
          "question": "HashMap 在多线程环境下会有什么问题？",
          "answer": "HashMap 非线程安全，多线程下可能的问题：(1) JDK 7 中并发扩容可能产生环形链表，导致 get() 死循环；(2) put() 时多线程同时触发扩容，可能丢失数据；(3) size 计数不准确。解决方案：使用 ConcurrentHashMap（推荐）、Collections.synchronizedMap()、或 Hashtable（不推荐，锁粒度太粗）。",
          "difficulty": 2
        },
        {
          "id": "jb-012",
          "question": "ConcurrentHashMap 在 JDK 7 和 JDK 8 中的实现有什么区别？",
          "answer": "JDK 7：分段锁（Segment），每个 Segment 继承 ReentrantLock，包含若干桶，不同 Segment 可并行操作。默认 16 个 Segment，并发度最多 16。JDK 8：放弃分段锁，改为 CAS + synchronized 细粒度锁，锁住单个桶的头节点。数据结构与 HashMap 一致（数组+链表+红黑树）。size 控制用 LongAdder（Cell 数组 + base，减少 CAS 竞争）。key 和 value 都不允许为 null（HashMap 允许）。",
          "difficulty": 3
        },
        {
          "id": "jb-013",
          "question": "fail-fast 和 fail-safe 机制是什么？",
          "answer": "fail-fast：迭代器遍历时，如果其他线程修改了集合结构（增删元素），会抛出 ConcurrentModificationException。通过 modCount 和 expectedModCount 比较实现。ArrayList、HashMap 等都是 fail-fast。fail-safe：迭代器遍历在集合的副本上进行，原集合修改不影响迭代。不会抛异常，但看不到修改后的数据。java.util.concurrent 包下的集合（如 CopyOnWriteArrayList、ConcurrentHashMap 的迭代器）是 fail-safe。",
          "difficulty": 2
        },
        {
          "id": "jb-014",
          "question": "HashSet 是如何保证元素不重复的？LinkedHashSet 和 TreeSet 有什么特点？",
          "answer": "HashSet 底层基于 HashMap 实现，add() 将元素作为 key 放入 HashMap，value 是一个固定的 PRESENT 对象。通过 hashCode() + equals() 判断重复。LinkedHashSet：继承 HashSet，内部用 LinkedHashMap（维护双向链表），保证插入顺序（或访问顺序）。TreeSet：基于 TreeMap（红黑树），元素自动排序，实现 Comparable 或传入 Comparator。三者时间复杂度：HashSet O(1)，LinkedHashSet O(1)，TreeSet O(log n)。",
          "difficulty": 2
        },
        {
          "id": "jb-015",
          "question": "Java 泛型擦除是什么？泛型在运行时还存在吗？通配符 <? extends T> 和 <? super T> 的区别？",
          "answer": "泛型擦除（Type Erasure）：编译器将泛型信息移除，运行时只有原始类型（raw type）。List<String> 和 List<List> 在运行时都是 List。泛型不存在于运行时字节码中，无法通过反射获取泛型参数（但可通过 TypeToken 等技巧间接获取）。<? extends T>（上界通配符）：只能读取为 T 类型，不能写入（PECS 原则 Producer Extends）。<? super T>（下界通配符）：可以写入 T 及其子类型，只能读出为 Object 类型（Consumer Super）。典型应用：Collections.copy(dst, src)，src 用 ? extends T（只读），dst 用 ? super T（只写）。",
          "difficulty": 2
        },
        {
          "id": "jb-016",
          "question": "Java 异常体系是怎样的？Checked Exception 和 Unchecked Exception 的设计争议？",
          "answer": "异常层次：Throwable → Error（JVM 层面错误，如 OutOfMemoryError）和 Exception。Exception 分为 Checked Exception（编译期必须处理：IOException、SQLException 等）和 Unchecked Exception（RuntimeException 子类：NullPointerException、IllegalArgumentException 等）。Checked Exception 的争议：(1) 强制处理增加代码复杂度（try-catch 或 throws 链式传播）；(2) 实际开发中常被吞掉（空 catch）；(3) 函数签名变化影响所有调用方。Lombok @SneakyThrows 可绕过检查。最佳实践：自定义业务异常继承 RuntimeException，对外统一封装返回。",
          "difficulty": 2
        },
        {
          "id": "jb-017",
          "question": "BlockingQueue 有哪些常见实现？各自适用什么场景？",
          "answer": "ArrayBlockingQueue：有界数组队列，必须指定容量，公平/非公平锁可选，适合固定容量场景。LinkedBlockingQueue：无界链表队列（可设最大值，默认 Integer.MAX_VALUE），吞吐量高于 ArrayBlockingQueue（锁粒度细），生产者消费者模式常用。PriorityBlockingQueue：无界优先级队列，元素需实现 Comparable 或传入 Comparator。DelayQueue：延迟获取元素，元素须实现 Delayed 接口（如定时任务调度）。SynchronousQueue：不存储元素的队列，每个 put 必须等一个 take，用于线程间直接交接（ThreadPoolExecutor.CallerRunsPolicy 类似效果）。ConcurrentLinkedQueue：非阻塞无界并发队列，基于 CAS。",
          "difficulty": 2
        },
        {
          "id": "jb-018",
          "question": "Java NIO 与传统 BIO 的核心区别？三大组件是什么？Netty 是怎么用 NIO 的？",
          "answer": "BIO（Blocking I/O）：每个连接一个线程，线程阻塞等待 I/O 完成，连接数受限于线程数。NIO（Non-blocking I/O）：多路复用，单线程管理多个连接（Selector），I/O 操作非阻塞。三大组件：Channel（通道，数据传输管道）、Buffer（缓冲区，数据容器，ByteBuffer 最常用）、Selector（选择器，轮询注册在其上的 Channel 就绪事件——OP_READ/OP_WRITE/OP_ACCEPT/OP_CONNECT）。NIO 核心模型：Reactor 模式（事件驱动）。Netty 基于 NIO 封装：Boss Group 负责 Accept，Worker Group 负责 Read/Write，ChannelPipeline 处理 Handler 链，ByteBuf 优化 ByteBuffer（池化、零拷贝、复合缓冲区）。",
          "difficulty": 3
        }
      ]
    },
    {
      "id": "java-concurrent",
      "label": "Java 并发",
      "icon": "🔄",
      "description": "线程、锁、线程池、并发工具",
      "source": "java.md",
      "questions": [
        {
          "id": "jc-001",
          "question": "Java 线程的生命周期有哪些状态？状态之间如何转换？",
          "answer": "Java 线程有 6 种状态（Thread.State）：NEW（新建）、RUNNABLE（可运行，包含就绪和运行中）、BLOCKED（阻塞，等待获取锁）、WAITING（无限期等待，如 Object.wait()）、TIMED_WAITING（限期等待，如 Thread.sleep()）、TERMINATED（终止）。转换路径：NEW → start() → RUNNABLE；RUNNABLE → 获取锁失败 → BLOCKED → 获得锁 → RUNNABLE；RUNNABLE → wait()/join() → WAITING → notify()/notifyAll() → RUNNABLE；RUNNABLE → sleep()/wait(timeout) → TIMED_WAITING → 超时/唤醒 → RUNNABLE；RUNNABLE → run()结束 → TERMINATED。",
          "difficulty": 2
        },
        {
          "id": "jc-002",
          "question": "死锁产生的条件是什么？如何检测、预防和避免死锁？",
          "answer": "死锁四个必要条件：互斥、持有并等待、不可剥夺、循环等待。检测：jstack 查看线程状态（FOUND ONE JAVA-LEVEL DEADLOCK），或使用 Arthas 的 thread 命令。预防策略：(1) 固定加锁顺序（破坏循环等待）；(2) 一次性获取所有锁（破坏持有并等待）；(3) 设置锁超时（tryLock 破坏不可剥夺）；(4) 减小锁粒度，缩短持锁时间。避免：使用并发工具类替代手动加锁（如 ConcurrentHashMap、CountDownLatch）。",
          "difficulty": 2
        },
        {
          "id": "jc-003",
          "question": "Java 内存模型（JMM）是什么？volatile 关键字能保证什么？",
          "answer": "JMM（Java Memory Model）规定了线程与主内存之间的抽象关系：每个线程有工作内存（CPU 缓存/寄存器），共享变量存储在主内存。JMM 定义了 happens-before 规则来保证可见性和有序性。volatile 保证：(1) 可见性：写入立即刷新到主内存，读取从主内存获取；(2) 禁止指令重排序（内存屏障）。但不保证原子性（如 i++ 仍不安全）。适用场景：状态标志位（boolean flag）、DCL 单例的双重检查。使用场景有限，需要原子性时用 Atomic 类或锁。",
          "difficulty": 3
        },
        {
          "id": "jc-004",
          "question": "synchronized 的底层原理是什么？锁升级过程是怎样的？",
          "answer": "synchronized 底层基于 Monitor 对象实现，通过 monitorenter/monitorexit 字节码指令加锁解锁。锁升级过程（JDK 6+）：无锁 → 偏向锁（第一个线程访问，记录线程 ID，无 CAS 开销）→ 轻量级锁（有竞争时，CAS 替换对象头的锁记录指针，自旋等待）→ 重量级锁（自旋超过阈值，升级为 OS 互斥量，涉及用户态/内核态切换）。Java 15 之后偏向锁被废弃（默认禁用），因为维护成本高且实际收益有限。",
          "difficulty": 3
        },
        {
          "id": "jc-005",
          "question": "ReentrantLock 和 synchronized 的区别？分别在什么场景下使用？",
          "answer": "ReentrantLock 相比 synchronized 的优势：(1) 可中断（lockInterruptibly()）；(2) 支持公平锁和非公平锁；(3) 支持超时获取（tryLock(timeout)）；(4) 支持多个 Condition（await/signal）；(5) 可以获取锁状态信息（isLocked、getQueueLength）。synchronized 的优势：(1) 用法简洁，JVM 自动释放锁；(2) JVM 层面优化（锁升级、锁粗化、锁消除）；(3) 异常自动释放锁。选择建议：简单同步用 synchronized，需要高级功能（可中断、超时、公平、多条件）用 ReentrantLock。",
          "difficulty": 3
        },
        {
          "id": "jc-006",
          "question": "CAS 是什么？ABA 问题如何解决？Atomic 类的原理？",
          "answer": "CAS（Compare-And-Swap）：乐观锁实现，比较当前值与期望值是否相同，相同则更新为 newValue，否则重试。底层通过 CPU 的 cmpxchg 指令实现原子操作。ABA 问题：值从 A→B→A，CAS 以为没变过就错误更新。解决：AtomicStampedReference 使用版本号标记，每次修改版本号+1。Atomic 原理：基于 Unsafe 类的 CAS 操作实现，AtomicInteger 用 volatile int + CAS 自旋。其他 Atomic 类：AtomicReference、AtomicLong、AtomicIntegerArray、LongAdder（高并发计数优化）。",
          "difficulty": 3
        },
        {
          "id": "jc-007",
          "question": "线程池的核心参数有哪些？拒绝策略有哪几种？",
          "answer": "核心参数：corePoolSize（核心线程数）、maximumPoolSize（最大线程数）、workQueue（任务队列）、keepAliveTime（空闲线程存活时间）、unit（时间单位）、threadFactory（线程工厂）、handler（拒绝策略）。执行流程：任务来时 → 核心线程未满则创建核心线程 → 核心线程满则入队列 → 队列满则创建非核心线程（直到最大线程数）→ 都满则执行拒绝策略。四种拒绝策略：AbortPolicy（抛异常，默认）、CallerRunsPolicy（调用者线程执行）、DiscardPolicy（直接丢弃）、DiscardOldestPolicy（丢弃队列最老的任务）。",
          "difficulty": 2
        },
        {
          "id": "jc-008",
          "question": "AQS 的原理是什么？Semaphore、CountDownLatch、CyclicBarrier 分别怎么用？",
          "answer": "AQS（AbstractQueuedSynchronizer）核心：一个 volatile int state + CLH 双向等待队列。子类通过重写 tryAcquire/tryRelease 来定义获取/释放同步状态的逻辑。Semaphore：控制并发访问数量，基于 AQS 的共享模式，acquire() 获取许可（state-1），release() 释放许可（state+1）。CountDownLatch：让一个或多个线程等待其他线程完成操作，基于 AQS 的共享模式，countDown() 使计数器-1，await() 在计数器归零前阻塞。CyclicBarrier：让一组线程到达屏障点后同时继续，可重复使用。区别：CountDownLatch 是一次性的，CyclicBarrier 可重置；CountDownLatch 是计数器递减，CyclicBarrier 是等待线程到齐。",
          "difficulty": 3
        },
        {
          "id": "jc-009",
          "question": "ThreadLocal 的原理？内存泄漏问题如何解决？",
          "answer": "ThreadLocal 为每个线程维护独立的变量副本，内部通过 ThreadLocalMap（Thread 的成员变量）实现，key 是 ThreadLocal 的弱引用（WeakReference），value 是强引用。内存泄漏原因：ThreadLocal 对象被回收后，key 变为 null，但 value 仍被 ThreadLocalMap 的 Entry 强引用，无法回收。解决方案：(1) 使用完后调用 remove()；(2) 使用 try-finally 保证清理；(3) 阿里开源 TransmittableThreadLocal（TTL）解决线程池场景下的传递问题。",
          "difficulty": 3
        },
        {
          "id": "jc-010",
          "question": "CompletableFuture 的主要用法？如何编排异步任务？",
          "answer": "CompletableFuture 是 Java 8 引入的异步编程工具。创建：supplyAsync（有返回值）、runAsync（无返回值），可指定线程池。转换：thenApply（同步转换结果）、thenCompose（异步扁平化，类似 flatMap）。消费：thenAccept（消费结果）、thenRun（执行后续任务）。组合：thenCombine（合并两个 future 结果）、allOf（等待全部完成）、anyOf（任一完成即返回）。异常处理：exceptionally（异常时提供默认值）、handle（统一处理正常和异常结果）。建议：始终传入自定义线程池，避免使用 ForkJoinPool.commonPool()。",
          "difficulty": 3
        },
        {
          "id": "jc-011",
          "question": "Java 虚拟线程（Virtual Thread，Java 21+）是什么？与传统线程的区别？",
          "answer": "虚拟线程（JEP 444）：轻量级线程，由 JVM 调度而非 OS。创建成本低（可轻松创建百万级），由平台线程（Carrier Thread）承载执行。阻塞操作会自动卸载虚拟线程到堆上，释放 Carrier Thread 执行其他虚拟线程。与 Thread Pool 的区别：传统线程池用少量线程处理大量任务（复用），虚拟线程为每个任务创建一个线程（替换）。适用场景：I/O 密集型任务（数据库查询、HTTP 请求、RPC 调用）。不适合 CPU 密集型计算（仍受 CPU 核心数限制）。使用方式：Thread.startVirtualThread(() -> ...) 或 Executors.newVirtualThreadPerTaskExecutor()。",
          "difficulty": 2
        },
        {
          "id": "jc-012",
          "question": "什么是 JMM 的 happens-before 规则？volatile、synchronized、final 各自如何保证可见性/有序性？",
          "answer": "happens-before 规则定义了多线程中操作的可见性保证：(1) 程序顺序规则；(2) volatile 写 happens-before 后续读；(3) synchronized 解锁 happens-before 后续加锁；(4) 线程 start() happens-before 该线程的任何操作；(5) 线程的所有操作 happens-before 其他线程从 join() 返回；(6) Thread 构造函数的最后动作 happens-before 线程 start()。volatile 保证可见性和有序性但不保证原子性（内存屏障）。synchronized 保证互斥访问 + 内存屏障（解锁前刷主内存，加锁时清空工作内存）。final 字段在构造函数完成后对其他线程可见（JSR-133 final 语义）。",
          "difficulty": 3
        }
      ]
    },
    {
      "id": "jvm",
      "label": "JVM",
      "icon": "⚙️",
      "description": "内存模型、GC、类加载",
      "source": "java.md",
      "questions": [
        {
          "id": "jvm-001",
          "question": "JVM 运行时数据区有哪些？各自的特点？",
          "answer": "运行时数据区分为线程共享和线程私有两部分。线程共享：堆（Heap，存放对象实例，GC 主战场）、方法区/元空间（Method Area/Metaspace，存放类信息、常量、静态变量）。线程私有：程序计数器（Program Counter，当前执行的字节码行号）、虚拟机栈（VM Stack，栈帧包含局部变量表、操作数栈、动态链接、返回地址）、本地方法栈（Native Method Stack，为 Native 方法服务）。JDK 8 后方法区从永久代移到元空间（使用本地内存），解决永久代 OOM 问题。",
          "difficulty": 2
        },
        {
          "id": "jvm-002",
          "question": "Java 对象的创建流程？内存布局是怎样的？对象访问方式有几种？",
          "answer": "创建流程：类加载检查 → 分配内存（指针碰撞/空闲列表）→ 初始化零值 → 设置对象头（Mark Word + 类型指针）→ 执行构造方法。内存布局：对象头（Mark Word：hash、GC 年龄、锁标志位；类型指针：指向类元数据）+ 实例数据（字段值）+ 对齐填充（8 字节对齐）。访问方式：句柄访问（堆中句柄池 → 对象，对象移动时只改句柄指针）和直接指针访问（引用直接指向对象，速度快，HotSpot 采用）。",
          "difficulty": 2
        },
        {
          "id": "jvm-003",
          "question": "JVM 如何判断对象可以被回收？四种引用类型是什么？",
          "answer": "判断方法：(1) 引用计数法（有循环引用问题，Java 不用）；(2) 可达性分析（GC Roots 开始向下搜索，不可达的对象可回收）。GC Roots 包括：虚拟机栈中的局部变量、静态变量、常量、本地方法栈中的 JNI 引用。四种引用：强引用（Object obj = new Object()，永不回收）、软引用（SoftReference，内存不足时回收，适合缓存）、弱引用（WeakReference，下次 GC 时回收，ThreadLocal 的 key）、虚引用（PhantomReference，不影响回收，用于跟踪 GC 活动）。",
          "difficulty": 2
        },
        {
          "id": "jvm-004",
          "question": "常见的 GC 算法有哪些？分代收集理论是什么？",
          "answer": "GC 算法：(1) 标记-清除（Mark-Sweep）：标记后直接清除，产生内存碎片；(2) 复制算法（Copying）：将存活对象复制到另一半，适合存活率低的区域（新生代），空间换时间；(3) 标记-整理（Mark-Compact）：标记后将存活对象移到一端，解决碎片问题（老年代）。分代收集：新生代（Eden + Survivor From/To）用复制算法（Minor GC/Young GC），老年代用标记-清除或标记-整理（Major GC/Full GC）。大部分对象朝生夕灭，熬过多次 Minor GC 的进入老年代。",
          "difficulty": 2
        },
        {
          "id": "jvm-005",
          "question": "从 Serial 到 ZGC，垃圾收集器的演进过程？各自的适用场景？",
          "answer": "Serial：单线程，STW，适合客户端模式。ParNew：多线程版 Serial，常与 CMS 配合。Parallel Scavenge：吞吐量优先的多线程收集器。CMS（Concurrent Mark Sweep）：以最短停顿为目标，标记-清除算法，四阶段（初始标记→并发标记→重新标记→并发清除），缺点：内存碎片、浮动垃圾。G1（Garbage First）：基于 Region 的分代收集，可预测停顿时间，混合回收（Young GC + Mixed GC），JDK 9 默认。ZGC：着色指针 + 读屏障，停顿 < 1ms（亚毫秒级），支持 TB 级堆，JDK 15 生产可用。Shenandoah：与 ZGC 类似的低延迟收集器。",
          "difficulty": 3
        },
        {
          "id": "jvm-006",
          "question": "双亲委派模型是什么？如何打破双亲委派？",
          "answer": "双亲委派：类加载器收到加载请求时，先委派给父加载器尝试加载，只有父加载器无法加载时才自己加载。层次：Bootstrap ClassLoader（rt.jar）→ Extension ClassLoader（ext 目录）→ Application ClassLoader（classpath）→ 自定义 ClassLoader。好处：保证类的唯一性和安全性（如 String 类不会被篡改）。打破方式：(1) SPI 机制（如 JDBC、JNDI），使用线程上下文类加载器（Thread Context ClassLoader）；(2) Tomcat 每个 WebApp 有独立 ClassLoader，先自己加载再委派；(3) OSGi 模块化，网状而非树状委派。",
          "difficulty": 3
        },
        {
          "id": "jvm-007",
          "question": "线上遇到 OOM 怎么排查？常用的 JVM 排查工具有哪些？",
          "answer": "OOM 排查步骤：(1) 保留现场：-XX:+HeapDumpOnOutOfMemoryError 自动生成堆转储；(2) 分析堆转储：使用 MAT（Memory Analyzer Tool）、jvisualvm 或 jhat 分析大对象和引用链；(3) 查看日志：GC 日志分析（-Xlog:gc*）、dmesg 查看系统日志确认是否被 OS OOM Killer 杀掉。常用工具：jps（进程列表）、jstat（GC 统计）、jmap（堆信息、histo/dump）、jstack（线程栈、死锁检测）、Arthas（在线诊断：dashboard、thread、heapdump、profiler）。",
          "difficulty": 2
        },
        {
          "id": "jvm-008",
          "question": "G1 垃圾收集器的回收流程？Mixed GC 和 Young GC 的区别？",
          "answer": "G1 将堆划分为多个 Region（通常 2048 个，每个 1~32MB），Region 分为 Eden、Survivor、Old、Humongous（超大对象，超过 Region 一半）。Young GC（YGC）：只回收 Eden + Survivor Region，STW 停顿可控。Mixed GC：除了 Young Region，还选择性回收部分 Old Region（基于 G1 的 Garbage First 策略——优先回收垃圾最多的 Region，即「收益最大」的 Region）。触发条件：老年代占用达到阈值（IHOP Initiating Heap Occupancy Percent，默认 45%）。G1 通过 -XX:MaxGCPauseMillis 设置目标停顿时间（默认 200ms），动态调整每次回收的 Region 数量。G1 不需要设置年轻代/老年代大小比例。",
          "difficulty": 3
        },
        {
          "id": "jvm-009",
          "question": "ZGC 为什么能做到亚毫秒级停顿？着色指针和读屏障是什么？",
          "answer": "ZGC 核心创新：(1) 着色指针（Colored Pointer）：在 64 位指针的高位中嵌入元数据（Finalizable、Remapped、Marked1、Marked0），使得 ZGC 能在移动对象的同时并发处理引用更新，无需 STW。（2） 读屏障（Load Barrier）：当线程读取引用时检查指针颜色标记，如果发现对象正在移动则修正引用。ZGC 回收阶段：并发标记 → 并发转移准备 → 并发转移（对象移动和引用更新同时进行），全程几乎无 STW（仅初始标记和最终标记有极短 STW，<1ms）。代价：需要 CPU 指令支持（Compressed Oops 兼容问题）、内存开销略大。",
          "difficulty": 3
        },
        {
          "id": "jvm-010",
          "question": "JVM 类加载过程是怎样的？类加载器有哪些？如何自定义类加载器？",
          "answer": "类加载三阶段：加载（.class 文件读到方法区生成 Class 对象）→ 验证（文件格式/字节码/符号引用验证）→ 准备（静态变量赋默认值）→ 解析（符号引用替换为直接引用）→ 初始化（执行 <clinit> 方法，静态变量赋值和 static 块执行）。类加载器：BootstrapClassLoader（C++ 实现，加载 rt.jar）、Ext/PlatformClassLoader（加载 jre/lib/ext 或 java.platform.ext.dirs）、AppClassLoader（加载 classpath）、自定义 ClassLoader。自定义步骤：继承 ClassLoader → 重写 findClass()（不要重写 loadClass() 以保留双亲委派）→ defineClass() 加载字节码。应用场景：热部署（Tomcat 每个 WebApp 有独立 ClassLoader）、加密类加载、模块隔离（OSGi）。",
          "difficulty": 2
        },
        {
          "id": "jvm-011",
          "question": "常见的 JVM 调优参数有哪些？生产环境推荐配置？",
          "answer": "内存参数：-Xms/-Xmx（堆大小，建议相等避免扩容）、-Xmn 或 -XX:NewRatio=2（新生代占比）、-MaxMetaspaceSize（元空间上限）。GC 参数：-XX:+UseG1GC（推荐 G1）、-XX:MaxGCPauseMillis=200（目标停顿时间）。GC 日志：-Xlog:gc*:file=gc.log:time,uptime:filecount=5,filesize=20m。其他有用参数：-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/dump.hprof（OOM 自动 dump）；-XX:+PrintGCDetails -XX:+PrintGCDateStamps（打印详细 GC 日志）；-Djava.security.egd=file:/dev/urandom（加速随机数生成）。生产建议：容器化环境注意限制内存（-XX:MaxRAMPercentage）、预留足够非堆内存（元空间+线程栈+直接内存）。",
          "difficulty": 3
        },
        {
          "id": "jvm-012",
          "question": "直接内存（Direct Memory）是什么？与堆内内存的区别？什么场景会用到？",
          "answer": "直接内存（堆外内存）：通过 Unsafe.allocateMemory() 或 ByteBuffer.allocateDirect() 分配的内存，不受 GC 管理（但可通过 PhantomReference 清理），不占用 Java 堆空间。与堆内内存区别：(1) 分配和释放成本更高（系统调用 malloc/free）；(2) 避免 Java 堆和 Native 堆之间的数据拷贝（零拷贝）；(3) 受 -XX:MaxDirectMemorySize 限制（默认等于 Xmx）。适用场景：(1) NIO 的 DirectByteBuffer（Netty/Kafka 大量使用）；(2) 大文件的内存映射（MappedByteBuffer/mmap）；(3) 避免大对象导致频繁 Full GC（如缓存数据放堆外）。风险：泄漏后无法通过常规工具排查，需监控 Direct Memory 使用量。",
          "difficulty": 3
        }
      ]
    },
    {
      "id": "spring",
      "label": "Spring 全家桶",
      "icon": "🌿",
      "description": "IoC、AOP、事务、Spring Boot",
      "source": "spring.md",
      "questions": [
        {
          "id": "sp-001",
          "question": "Spring IoC 解决了什么问题？@Component 和 @Bean 有什么区别？",
          "answer": "IoC（控制反转）将对象的创建和依赖管理从代码中解耦，交由容器管理。程序员通过配置/注解声明依赖关系，容器负责实例化和注入。@Component：标注在类上，由 Spring 自动扫描并实例化（类由 Spring 管理）。@Bean：标注在方法上，返回一个对象交由 Spring 管理（方法由程序员控制创建逻辑），适用于第三方库的类或需要复杂初始化的对象。@Bean 通常搭配 @Configuration 使用。",
          "difficulty": 1
        },
        {
          "id": "sp-002",
          "question": "@Autowired 和 @Resource 的区别？",
          "answer": "@Autowired：Spring 注解，默认按类型（byType）注入。同一类型有多个 Bean 时配合 @Qualifier 指定名称。可用于构造器、Setter、字段。@Resource：JSR-250 标准注解，默认按名称（byName）注入，找不到再按类型。可通过 name 属性指定 Bean 名称。选择建议：团队统一规范即可，Spring 项目中 @Autowired 更常见，需要按名称注入时用 @Resource 或 @Autowired + @Qualifier。",
          "difficulty": 1
        },
        {
          "id": "sp-003",
          "question": "构造器注入、Setter 注入、字段注入各有什么优缺点？",
          "answer": "构造器注入（推荐）：依赖不可变（final）、完全初始化、利于测试（无需反射设值）、明确依赖关系。Setter 注入：可选依赖、灵活可重新配置。字段注入：代码简洁但弊端多——无法声明 final、隐藏依赖关系、不利于测试（需要反射）、Spring 不推荐。Spring 官方推荐构造器注入，必要依赖用构造器，可选依赖用 Setter。",
          "difficulty": 2
        },
        {
          "id": "sp-004",
          "question": "Spring Bean 的生命周期是怎样的？有哪些扩展点？",
          "answer": "完整生命周期：实例化（createBeanInstance）→ 属性注入（populateBean）→ Aware 回调（BeanNameAware、BeanFactoryAware等）→ BeanPostProcessor#postProcessBeforeInitialization → 初始化方法（@PostConstruct、InitializingBean#afterPropertiesSet、自定义 init-method）→ BeanPostProcessor#postProcessAfterInitialization → 使用 → 销毁（@PreDestroy、DisposableBean#destroy、自定义 destroy-method）。常用扩展点：BeanPostProcessor（AOP 代理在此创建）、BeanFactoryPostProcessor（修改 Bean 定义）。",
          "difficulty": 2
        },
        {
          "id": "sp-005",
          "question": "Spring AOP 的核心概念？Spring AOP 和 AspectJ 的区别？",
          "answer": "核心概念：切面（Aspect，横切关注点的模块化）、切点（Pointcut，定义哪些方法被拦截）、通知（Advice，切面在特定点的行为：@Before/@After/@AfterReturning/@AfterThrowing/@Around）、连接点（JoinPoint，方法执行点）。Spring AOP：运行时代理（JDK 动态代理或 CGLIB），只支持方法级别的切面，Spring 内置支持。AspectJ：编译期/类加载期织入，支持字段、构造器等更细粒度，功能更强大但使用复杂。日常开发 Spring AOP 足够用。",
          "difficulty": 2
        },
        {
          "id": "sp-006",
          "question": "同类内部方法调用 AOP 为什么会失效？如何解决？",
          "answer": "原因：Spring AOP 基于代理，外部调用走代理对象，代理对象增强方法；同类内部调用 this.method() 是直接调用目标对象，不经过代理，AOP 不生效。解决方案：(1) 自注入（@Autowired 注入自身，但不推荐，容易循环依赖）；(2) AopContext.currentProxy() 获取代理对象调用（需设置 @EnableAspectJAutoProxy(exposeProxy=true)）；(3) 将方法拆到另一个 Bean 中（推荐）；(4) 通过 ApplicationContext 获取代理对象。",
          "difficulty": 3
        },
        {
          "id": "sp-007",
          "question": "DispatcherServlet 的工作流程？拦截器和过滤器的区别？",
          "answer": "DispatcherServlet 流程：请求到达 → HandlerMapping 根据 URL 找到 Handler → HandlerAdapter 执行 Handler → 执行拦截器 preHandle → 执行 Controller 方法 → 执行拦截器 postHandle → 视图渲染 → 执行拦截器 afterCompletion → 返回响应。过滤器（Filter）：Servlet 规范，在 DispatcherServlet 之前/之后执行，可用于请求/响应的通用处理（编码、CORS、日志）。拦截器（Interceptor）：Spring 机制，在 Handler 前后执行，可以访问 Handler 和 ModelAndView，更细粒度。",
          "difficulty": 2
        },
        {
          "id": "sp-008",
          "question": "@Transactional 的七种传播行为？事务失效的常见场景？",
          "answer": "传播行为：REQUIRED（默认，有事务则加入，无则新建）、REQUIRES_NEW（总是新建事务，挂起当前事务）、NESTED（嵌套事务，外层回滚影响内层，内层回滚不影响外层）、SUPPORTS（有事务则加入，无则非事务执行）、NOT_SUPPORTED（非事务执行，挂起当前事务）、MANDATORY（必须在事务中，否则异常）、NEVER（不能在事务中，否则异常）。失效场景：(1) 同类内部调用；(2) 方法非 public；(3) 异常被 try-catch 吞掉；(4) 异常类型不匹配（默认只回滚 RuntimeException）；(5) 数据库引擎不支持事务（如 MyISAM）；(6) 未被 Spring 代理（new 出来的对象）。",
          "difficulty": 3
        },
        {
          "id": "sp-009",
          "question": "Spring 如何通过三级缓存解决循环依赖？构造器注入为什么无法自动解决？",
          "answer": "三级缓存：singletonObjects（一级，完整 Bean）、earlySingletonObjects（二级，提前暴露的 Bean 引用）、singletonFactories（三级，Bean 工厂，可生成提前引用）。流程：A 创建 → A 的工厂放入三级缓存 → A 属性注入发现依赖 B → B 创建 → B 属性注入发现依赖 A → 从三级缓存获取 A 的早期引用（如有 AOP 则创建代理）→ B 初始化完成 → A 注入 B → A 初始化完成。构造器注入：对象还未创建完成就需注入依赖，无法放入三级缓存提前暴露，需 @Lazy 注解。",
          "difficulty": 3
        },
        {
          "id": "sp-010",
          "question": "Spring Boot 自动配置的原理是什么？条件装配是怎么工作的？",
          "answer": "自动配置原理：@SpringBootApplication 包含 @EnableAutoConfiguration，通过 SpringFactoriesLoader 加载 META-INF/spring.factories（或 JDK 17 的 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports）中列出的自动配置类。条件装配：通过 @Conditional 系列注解控制配置生效条件，如 @ConditionalOnClass（类路径存在某类）、@ConditionalOnMissingBean（容器中不存在某 Bean）、@ConditionalOnProperty（配置项匹配）。自动配置类通常会检查用户是否已自定义 Bean，存在则不覆盖。",
          "difficulty": 3
        },
        {
          "id": "sp-011",
          "question": "Spring Boot 配置文件加载优先级是怎样的？多环境配置怎么管理？",
          "answer": "配置加载优先级（高→低）：命令行参数 > java 系统属性 (-D) > OS 环境变量 > application-{profile}.properties > application.properties > 默认值。多环境管理：(1) Profile 激活：--spring.profiles.active=dev；(2) 多文件：application-dev.yml / application-prod.yml；(3) 配置中心集成：Nacos/Apollo 统一管理动态配置；(4) @Profile 注解：根据环境条件化加载 Bean。生产环境敏感信息不要硬编码到配置文件中，应使用环境变量或密钥管理系统。",
          "difficulty": 2
        },
        {
          "id": "sp-012",
          "question": "Spring 事件机制是什么？@TransactionalEventListener 怎么用？",
          "answer": "Spring 事件驱动：(1) 定义事件类继承 ApplicationEvent；(2) 发布事件：注入 ApplicationEventPublisher 调用 publishEvent()；(3) 监听事件：@EventListener 注解方法或实现 ApplicationListener。默认同步执行，加 @Async 可异步。@TransactionalEventListener：确保监听器在事务提交后才触发。phase 属性：BEFORE_COMMIT（提交前）、AFTER_COMMIT（提交后，默认）、AFTER_ROLLBACK（回滚后）、AFTER_COMPLETION（完成后）。典型应用：用户注册后发邮件+积分（解耦业务逻辑）、缓存刷新通知。注意：异步监听器如果事务回滚无法感知，需根据场景选择同步或异步。",
          "difficulty": 2
        },
        {
          "id": "sp-013",
          "question": "MyBatis 中 #{} 和 ${} 的区别？MyBatis-Plus 有哪些增强？",
          "answer": "#{} 是预编译参数占位符（PreparedStatement ?），防 SQL 注入。${} 是字符串直接拼接（Statement），有 SQL 注入风险，适用场景：动态表名/列名（如 ORDER BY ${column}），但必须自行校验输入合法性。MyBatis-Plus 增强：内置 CRUD（BaseMapper）、条件构造器 Wrapper（QueryWrapper/LambdaQueryWrapper）、分页插件 Page、逻辑删除填充、字段自动填充（MetaObjectHandler）、乐观锁插件（@Version）、代码生成器（AutoGenerator）。注意：MyBatis-Plus 的 LambdaQueryWrapper 避免了硬编码列名，类型安全。",
          "difficulty": 2
        }
      ]
    },
    {
      "id": "mysql",
      "label": "MySQL",
      "icon": "🗄️",
      "description": "索引、事务、MVCC、锁机制",
      "source": "mysql.md",
      "questions": [
        {
          "id": "my-001",
          "question": "B+ 树为什么适合做数据库索引？和B 树相比有什么优势？",
          "answer": "B+ 树特点：所有数据都在叶子节点，非叶子节点只存键值（扇出更大，树更矮）；叶子节点通过双向链表连接。优势：(1) 范围查询高效（遍历链表即可）；(2) 树高更低（通常 2~3 层），减少磁盘 I/O；(3) 单个节点可存更多键（扇出大），因为非叶子节点不存数据。适合磁盘索引：磁盘读写以页（Page，通常 16KB）为单位，B+ 树的节点大小恰好对应页大小，一次 I/O 读一个节点。",
          "difficulty": 2
        },
        {
          "id": "my-002",
          "question": "什么是覆盖索引？什么是最左前缀原则？索引下推（ICP）是什么？",
          "answer": "覆盖索引：查询的列全部包含在索引中，无需回表查数据行，Extra 显示 Using index。最左前缀：联合索引 (a, b, c) 遵循从左到右匹配，where a=1、where a=1 and b=2、where a=1 and b=2 and c=3 都能走索引，但 where b=2 或 where c=3 不能（除非有单独索引）。索引下推（Index Condition Pushdown，MySQL 5.6+）：在索引遍历过程中，对索引中包含但无法利用索引过滤的条件（如 b=2），直接在存储引擎层判断，减少回表次数。Extra 显示 Using index condition。",
          "difficulty": 2
        },
        {
          "id": "my-003",
          "question": "索引失效的常见场景有哪些？",
          "answer": "索引失效场景：(1) 对索引列使用函数（WHERE YEAR(create_time) = 2024 → 改为 create_time >= '2024-01-01'）；(2) 隐式类型转换（字符串列用数字查，如 phone = 13800000000）；(3) OR 条件中有非索引列；(4) LIKE 以通配符开头（LIKE '%abc'）；(5) 不符合最左前缀原则；(6) != 或 <> 操作符（优化器可能放弃索引）；(7) IS NOT NULL（部分场景）；(8) 对索引列做运算（WHERE id + 1 = 10）。优化建议：使用 EXPLAIN 验证执行计划。",
          "difficulty": 2
        },
        {
          "id": "my-004",
          "question": "MySQL 的事务隔离级别有哪些？InnoDB 如何解决幻读？",
          "answer": "四种隔离级别：读未提交（RU，脏读）、读已提交（RC，不可重复读）、可重复读（RR，MySQL 默认，解决不可重复读）、串行化（SERIALIZABLE，解决幻读但性能差）。InnoDB 在 RR 级别通过 MVCC + Next-Key Lock 解决幻读：(1) 普通读（快照读）通过 MVCC ReadView 保证一致性，不会看到其他事务新增的行；(2) 当前读（SELECT ... FOR UPDATE/LOCK IN SHARE MODE、UPDATE、DELETE）通过 Next-Key Lock（Record Lock + Gap Lock）锁住间隙，防止其他事务插入。注意：MVCC 无法完全解决幻读（快照读后执行当前读仍可能看到幻行）。",
          "difficulty": 3
        },
        {
          "id": "my-005",
          "question": "MVCC 的原理是什么？",
          "answer": "MVCC（Multi-Version Concurrency Control）核心组件：(1) 隐藏列：每行数据有 trx_id（最近修改的事务ID）和 roll_pointer（指向 undo log 中旧版本）；(2) Undo Log 版本链：通过 roll_pointer 将同一行数据的历史版本串成链表；(3) ReadView：事务执行快照读时创建的可见性判断规则，包含 m_ids（活跃事务列表）、min_trx_id、max_trx_id、creator_trx_id。RC 级别每次 SELECT 都生成新 ReadView，RR 级别事务中第一次 SELECT 生成后复用。判断规则：版本 trx_id < min_trx_id 可见；trx_id >= max_trx_id 不可见；min_trx_id ≤ trx_id < max_trx_id，不在 m_ids 中则可见。",
          "difficulty": 3
        },
        {
          "id": "my-006",
          "question": "InnoDB 的锁机制？Record Lock、Gap Lock、Next-Key Lock 分别是什么？",
          "answer": "InnoDB 锁粒度：表级锁（意向锁 IS/IX，快速判断表级冲突）和行级锁。行级锁类型：Record Lock（记录锁，锁定索引上的单条记录）、Gap Lock（间隙锁，锁定索引记录之间的间隙，防止插入）、Next-Key Lock（Record Lock + Gap Lock，左开右闭区间，RR 级别默认行锁形式）。意向锁作用：在加表级锁前先加意向锁，标识表中某行已被加锁，提高表锁的兼容性检查效率。IS/IX 与 S/X 的兼容矩阵：意向锁之间兼容，意向锁与表锁互斥。",
          "difficulty": 3
        },
        {
          "id": "my-007",
          "question": "Redo Log、Undo Log、Binlog 的区别和各自的作用？两阶段提交如何保证一致性？",
          "answer": "Redo Log（重做日志）：InnoDB 引擎层，记录物理页修改，用于 crash recovery（WAL 机制），保证持久性。Undo Log（回滚日志）：InnoDB 引擎层，记录数据修改前的值，用于事务回滚和 MVCC。Binlog（归档日志）：Server 层，记录逻辑操作（DDL/DML），用于主从复制和数据恢复。两阶段提交：Prepare 阶段将 Redo Log 写入磁盘（标记为 prepare 状态）→ 写入 Binlog → Commit 阶段将 Redo Log 标记为 commit。崩溃恢复规则：Redo Log 处于 prepare 且 Binlog 完整则提交；Redo Log 处于 prepare 但 Binlog 不完整则回滚。保证 Redo Log 和 Binlog 的一致性。",
          "difficulty": 3
        },
        {
          "id": "my-008",
          "question": "如何定位慢 SQL？深度分页怎么优化？",
          "answer": "慢 SQL 定位：开启 slow_query_log（slow_query_log=ON, long_query_time=1）；使用 pt-query-digest 分析慢查询日志；EXPLAIN 查看执行计划（关注 type、key、rows、Extra）。深度分页优化：LIMIT 1000000, 10 需要扫描 1000010 行再丢弃 1000000 行。优化方案：(1) 游标分页（WHERE id > last_id LIMIT 10），要求 id 有序；(2) 延迟关联（子查询先通过覆盖索引查出主键，再 JOIN 回表取数据）；(3) BETWEEN AND（适合连续 ID）。注意：游标分页不支持跳页。",
          "difficulty": 2
        },
        {
          "id": "my-009",
          "question": "InnoDB 和 MyISAM 的区别？什么场景选哪个？",
          "answer": "InnoDB：支持事务（ACID）、行级锁、外键、MVCC、崩溃恢复（Redo Log/Undo Log）。适合高并发 OLTP 场景（订单、支付、用户系统）。MyISAM：不支持事务和行锁，只支持表锁。支持全文索引（MyISAM 原生，InnoDB 5.6+ 也支持）、压缩表、空间索引。适合读多写少的场景（日志、文章表）、不需要事务的场景。性能差异：MyISAM 读性能略高（无事务开销），但写性能差（表锁）。现代项目几乎全部使用 InnoDB。MySQL 5.5+ 默认引擎就是 InnoDB。",
          "difficulty": 1
        },
        {
          "id": "my-010",
          "question": "EXPLAIN 执行计划中 type、key、rows、Extra 各字段怎么分析？",
          "answer": "type（访问类型，从好到差）：system/const（常量）> eq_ref（唯一索引）> ref（非唯一索引）> range（范围扫描）> index（索引扫描）> ALL（全表扫描）。至少达到 ref 级别。key：实际使用的索引名，NULL 表示未使用索引。rows：预估扫描的行数，越少越好。Extra 关键信息：Using index（覆盖索引，好）、Using index condition（索引下推）、Using filesort（额外排序，差）、Using temporary（临时表，差）、Using where（WHERE 过滤）。优化目标：消除 filesort 和 temporary，尽量达到 Using index。",
          "difficulty": 2
        },
        {
          "id": "my-011",
          "question": "MySQL 死锁如何检测和处理？如何预防死锁？",
          "answer": "死锁检测：InnoDB 通过 wait-for graph（等待图）自动检测死锁，检测到后回滚代价最小的事务。查看最近死锁：SHOW ENGINE INNODB STATUS。排查工具：SHOW PROCESSLIST（查看锁等待线程）、information_schema.INNODB_LOCK_WAITS、performance_schema.data_locks。预防策略：(1) 按固定顺序加锁（如总是先锁 id 小的行）；(2) 事务尽量短小（减少锁持有时间）；(3) 合理使用索引（避免行锁升级为表锁）；(4) 设置锁等待超时（innodb_lock_wait_timeout）；(5) 降低隔离级别（如 RC 级别间隙锁更少）。",
          "difficulty": 3
        },
        {
          "id": "my-012",
          "question": "分库分表后 SQL 路由和数据迁移怎么做？ShardingSphere 的核心概念？",
          "answer": "SQL 路由：ShardingSphere 解析 SQL → 根据分片策略（分片键 + 分片算法）确定目标数据源和表 → SQL 改写（如表名替换为物理表名）→ 在各分片执行 → 结果归并（合并、排序、分页）。分片算法：精确分片（= / IN）、范围分片（BETWEEN）、复合分片（多列）。数据迁移：全量导出 + 增量同步（Canal 监听 Binlog）→ 数据一致性校验 → 灰度切换。ShardingSphere 核心概念：ShardingRule（分片规则，包含 TableRule + BindingTableRule）、ShardingStrategy（分片策略，包含分片键 + 分片算法）、数据源配置。注意：跨片 JOIN 性能差，应通过冗余、ES 宽表或绑定表优化。",
          "difficulty": 3
        }
      ]
    },
    {
      "id": "database",
      "label": "数据库通用",
      "icon": "📊",
      "description": "事务、索引、分库分表、读写分离",
      "source": "database.md",
      "questions": [
        {
          "id": "db-001",
          "question": "ACID 的含义？各自是如何保证的？",
          "answer": "原子性（Atomicity）：事务要么全部成功要么全部回滚，通过 Undo Log 保证。一致性（Consistency）：事务前后数据满足约束和规则，由业务逻辑 + 数据库约束（主键、外键、唯一约束、CHECK）保证。隔离性（Isolation）：并发事务之间互不影响，通过锁和 MVCC 保证。持久性（Durability）：事务提交后数据永久保存，通过 Redo Log（WAL 机制）保证。",
          "difficulty": 2
        },
        {
          "id": "db-002",
          "question": "MVCC 的 ReadView 在 RC 和 RR 级别下有什么区别？",
          "answer": "RC（读已提交）：每次执行 SELECT 都会生成新的 ReadView，能读到其他事务已提交的最新数据（不可重复读）。RR（可重复读）：事务中第一次 SELECT 时生成 ReadView，后续复用，保证事务内多次读同一数据结果一致。区别本质：RC 每次 SELECT 都是最新快照，RR 是事务开始时的快照。这也是为什么 RC 解决脏读但解决不了不可重复读，RR 通过复用 ReadView 同时解决了脏读和不可重复读。",
          "difficulty": 3
        },
        {
          "id": "db-003",
          "question": "长事务有什么危害？如何避免？",
          "answer": "危害：(1) 锁持有时间长，阻塞其他事务，降低并发度；(2) Undo Log 膨胀（保存多个数据版本），占用大量空间；(3) MVCC 快照无法及时回收（RR 级别长事务阻止 purge 线程清理旧版本）。避免：(1) 将大事务拆成小事务；(2) 设置事务超时（innodb_lock_wait_timeout）；(3) 避免在事务中执行非数据库操作（如 RPC 调用、文件操作）；(4) 监控长事务（information_schema.INNODB_TRX，关注 trx_started 时间）。",
          "difficulty": 2
        },
        {
          "id": "db-004",
          "question": "唯一索引和普通索引在查询和写入性能上有什么区别？",
          "answer": "查询：InnoDB 是按页读取的，唯一索引查到一条记录后停止，普通索引查到不匹配的记录才停止。但由于数据是按页缓存的，两者差异通常不大。写入（Change Buffer 机制）：普通索引的插入/修改如果目标页不在内存中，Change Buffer 会缓存修改操作，等后续读取时合并，减少磁盘 I/O。唯一索引必须检查唯一性，必须先将数据页读入内存再判断，无法利用 Change Buffer。结论：写入频繁、对查询性能要求不极致的场景，普通索引写入性能更优。",
          "difficulty": 3
        },
        {
          "id": "db-005",
          "question": "分库分表的垂直拆分和水平拆分分别是什么？分片键如何选择？",
          "answer": "垂直拆分：按业务模块将不同业务的表拆分到不同数据库/实例（如用户库、订单库、商品库），解决单库 IO 和连接数瓶颈。水平拆分：同一张表按分片键将数据分散到多个表/库（如 order_0、order_1），解决单表数据量过大的问题。分片键选择原则：(1) 数据均匀分布（避免数据倾斜）；(2) 查询频率高的字段（减少跨片查询）；(3) 尽量避免跨片 JOIN 和跨片聚合。常用策略：Hash 取模、Range 范围、一致性 Hash。",
          "difficulty": 2
        },
        {
          "id": "db-006",
          "question": "分库分表后如何处理跨片查询？",
          "answer": "跨片 JOIN：避免跨片 JOIN，通过冗余字段、数据同步（ES 宽表）或将 JOIN 的表按相同分片键分片。跨片聚合：在各分片执行局部聚合（COUNT/SUM/MAX/MIN），然后在内存中做全局合并。全局排序：各分片局部排序，取各分片的 Top N 后在内存中全局排序。分页：跨片分页性能差（每片都需查 offset+size 条数据），优化方案同深度分页（游标分页/延迟关联）。中间件：ShardingSphere 提供 SQL 解析、路由、改写、归并能力。",
          "difficulty": 3
        },
        {
          "id": "db-007",
          "question": "主从复制原理是什么？主从延迟怎么应对？",
          "answer": "主从复制：主库将修改操作写入 Binlog，从库的 IO 线程请求主库的 Binlog（dump 线程发送），写入本地 Relay Log，从库的 SQL 线程重放 Relay Log。异步复制：主库写入 Binlog 即返回成功，不等待从库确认。半同步复制：至少一个从库确认接收后才返回成功。主从延迟应对：(1) 强制读主库（关键业务）；(2) 延迟检测与降级（超过阈值时切主库读）；(3) 并行复制（多线程回放 Relay Log，基于库/组提交并行）；(4) 读写分离中间件支持延迟感知路由。",
          "difficulty": 2
        },
        {
          "id": "db-008",
          "question": "Redis、MongoDB、Elasticsearch、HBase 的选型考量？",
          "answer": "Redis：全内存，数据结构丰富，适合缓存、会话、排行榜、分布式锁。不支持复杂查询，数据量受内存限制。MongoDB：文档模型，Schema 灵活，适合非结构化数据、快速迭代、内容管理。支持复杂查询但写入频率受限于索引。Elasticsearch：全文检索、倒排索引，适合日志分析、搜索引擎。写入成本高（需构建倒排索引），不适合高频写入。HBase/Cassandra：LSM-Tree，适合海量数据、高吞吐写入、时序数据。不支持复杂查询，查询只能通过 RowKey。选型维度：数据模型、读写模式、一致性要求、数据量、查询复杂度、运维复杂度。",
          "difficulty": 2
        },
        {
          "id": "db-009",
          "question": "EXPLAIN 执行计划中 type 和 Extra 字段分别怎么分析？",
          "answer": "type（访问类型，从好到差）：system/const > eq_ref > ref > range > index > ALL。eq_ref：唯一索引等值匹配（多表 JOIN）；ref：非唯一索引等值匹配；range：索引范围扫描（BETWEEN、>、<）；index：索引全扫描（比 ALL 好，从索引读取）；ALL：全表扫描（最差，必须优化）。Extra 关键标记：Using index（覆盖索引，无需回表）；Using index condition（索引下推 ICP）；Using filesort（额外排序，消耗 CPU 和内存）；Using temporary（创建临时表，通常在 GROUP BY 或 DISTINCT 时出现）；Using where（WHERE 子句过滤）。优化目标：消除 filesort 和 temporary。",
          "difficulty": 2
        },
        {
          "id": "db-010",
          "question": "批量 INSERT 如何优化？大数据量导入怎么做？",
          "answer": "批量优化：(1) 批量 INSERT 替代逐条插入（单次 INSERT 多行 VALUES，减少网络 RTT 和事务提交次数）；(2) INSERT ... ON DUPLICATE KEY UPDATE 实现Upsert（存在则更新，不存在则插入）；(3) 关闭自动提交（SET autocommit=0），一批数据在一个事务中提交。大数据量导入：LOAD DATA INFILE（比 INSERT 快 20~100 倍，直接读取文件跳过 SQL 解析）；分批次导入（每批 5000~10000 行）；导入前关闭索引和外键检查，导入后重建。注意事项：控制事务大小（避免长事务锁表）、关注 binlog 大小、监控主从延迟。",
          "difficulty": 2
        },
        {
          "id": "db-011",
          "question": "分布式主键方案有哪些？雪花算法、号段模式各自的优劣？",
          "answer": "方案对比：UUID：本地生成，无依赖，但无序导致索引性能差（随机写）、存储空间大（36字符 vs 18字符 Long）。数据库自增：简单但单点瓶颈，分库分表后不唯一。雪花算法（Snowflake）：64 位 Long（时间戳 + 机器ID + 序列号），趋势递增，索引友好，但依赖时钟。号段模式（Leaf）：数据库预分配号段（一次取 1000 个），应用内存中使用，用完再取。性能远优于数据库自增，但重启可能浪费号段。百度 UidGenerator / 美团 Leaf：结合数据库 + 本地缓存，支持双 Buffer 预分配。选型：单机用雪花算法，大规模用号段模式或混合方案。",
          "difficulty": 2
        },
        {
          "id": "db-012",
          "question": "ShardingSphere 的核心概念和使用方式？",
          "answer": "核心概念：逻辑表（应用使用的表名）→ 物理表（实际分片后的表，如 order_0、order_1）；分片键（决定数据路由的字段）；分片算法（哈希取模、范围、复合）。工作流程：SQL 解析 → 路由（根据分片键和算法确定目标分片）→ SQL 改写（逻辑表名替换为物理表名）→ 执行（在各分片执行）→ 结果归并（合并排序分页）。配置方式：YAML 配置分片规则（数据源、分片表、分片算法）。注意：跨片 JOIN 性能差，使用绑定表（BindingTableRule）优化关联查询；广播表（小表全量同步到所有分片，如字典表）。ShardingSphere-JDBC（嵌入应用）vs ShardingSphere-Proxy（独立代理服务）。",
          "difficulty": 3
        }
      ]
    },
    {
      "id": "db-design",
      "label": "数据库设计",
      "icon": "📐",
      "description": "建模、索引设计、冷热分层、扩容",
      "source": "db-design.md",
      "questions": [
        {
          "id": "dd-001",
          "question": "数据库三范式是什么？反范式设计在什么场景下使用？",
          "answer": "第一范式（1NF）：字段具有原子性，不可再分。第二范式（2NF）：在 1NF 基础上，消除部分依赖（非主属性完全依赖于主键）。第三范式（3NF）：在 2NF 基础上，消除传递依赖（非主属性不依赖于其他非主属性）。反范式设计：以空间换时间，通过适当冗余减少 JOIN 操作，提升查询性能。适用场景：读多写少、查询性能要求高、跨表关联频繁。如订单表冗余商品名称和价格，避免每次查询都 JOIN 商品表。注意控制冗余，保证更新一致性。",
          "difficulty": 2
        },
        {
          "id": "dd-002",
          "question": "如何设计高效的联合索引？",
          "answer": "联合索引设计原则：(1) 等值条件列在前，范围条件列在后（最左前缀匹配）；(2) 区分度高的列（COUNT(DISTINCT col)/COUNT(*) 接近 1）放在前面，过滤效果更好；(3) 覆盖索引设计：将查询所需的列全部纳入索引，消除回表；(4) 避免冗余索引（如已有 (a,b) 索引，(a) 就是冗余的）；(5) 考虑排序需求：ORDER BY 的列尽量纳入索引避免 filesort。示例：WHERE a = ? AND b = ? ORDER BY c DESC → 联合索引 (a, b, c DESC)。",
          "difficulty": 2
        },
        {
          "id": "dd-003",
          "question": "如何识别和处理冷热数据？",
          "answer": "冷热识别标准：访问频率（最近 N 天无访问）、业务状态（订单已完成且超过 N 天）、数据时间戳（日志类数据按时间划分）。处理策略：(1) 冷热分离：热数据在 MySQL/Redis，冷数据归档到历史库/对象存储/OSS/S3；(2) 数据生命周期管理（DLM）：TTL 自动过期、定期归档任务；(3) 分区表：RANGE 按时间分区，旧分区可直接 detach 或迁移，不影响新分区查询；(4) 压缩存储：冷数据压缩降低成本。",
          "difficulty": 2
        },
        {
          "id": "dd-004",
          "question": "大表扩容的平滑迁移流程是怎样的？",
          "answer": "平滑扩容流程：(1) 新分片/实例上线；(2) 双写：应用同时写入新旧库（保证数据不丢）；(3) 历史数据迁移：全量导出 + 增量同步（Canal 监听 Binlog 增量同步）；(4) 数据一致性校验：对比新旧库的行数和 checksum；(5) 灰度切换读流量到新库；(6) 全量切换读写到新库；(7) 观察一段时间后下线旧库。回滚预案：保留旧库只读窗口，切换失败时快速切回。关键点：增量同步的延迟监控、切流比例的渐进调整。",
          "difficulty": 3
        },
        {
          "id": "dd-005",
          "question": "软删除和硬删除各有什么利弊？审计字段如何设计？",
          "answer": "软删除（is_deleted 标记）：好处是数据可恢复、保留历史记录；弊端是查询需额外过滤（WHERE is_deleted = 0）、存储膨胀、唯一约束需额外处理（如唯一索引需包含 is_deleted）。硬删除：数据物理删除，释放存储空间，但不可恢复。审计字段设计：created_at（创建时间，默认 CURRENT_TIMESTAMP）、updated_at（更新时间，ON UPDATE CURRENT_TIMESTAMP）、created_by（创建人）、updated_by（更新人），可通过 MyBatis 拦截器或 JPA Auditing 自动填充。数据版本控制：乐观锁版本号（version 字段），UPDATE ... SET version = version + 1 WHERE id = ? AND version = ?。",
          "difficulty": 2
        },
        {
          "id": "dd-006",
          "question": "ER 建模的基本流程？宽表和窄表各自适合什么场景？",
          "answer": "ER 建模流程：(1) 识别实体（名词：用户、订单、商品）；(2) 定义属性（每个实体的字段）；(3) 确定关系（一对一：1:1、一对多：1:N、多对多：M:N），关系用外键实现；(4) M:N 关系需要中间表；(5) 验证范式和冗余合理性。宽表 vs 窄表：OLTP（事务处理）倾向窄表（范式化，减少冗余和更新异常）；OLAP（分析查询）倾向宽表（星型/雪花模型，反范式化，一张大表包含多维度字段，减少 JOIN 提升查询速度）。数据仓库常用宽表预计算。微服务架构中各服务维护窄表，数据分析平台通过 ETL 生成宽表。",
          "difficulty": 2
        },
        {
          "id": "dd-007",
          "question": "线上给大表加字段的方案？不停机怎么做？",
          "answer": "风险：大表（千万级以上）ALTER TABLE 会锁表，导致服务不可用。不停机方案：(1) MySQL 5.6+ Online DDL：大部分 ALTER 操作支持在线执行（ALGORITHM=INPLACE），但仍可能短暂锁表（如添加全文索引）。建议低峰期执行，设置 lock_wait_timeout。(2) pt-online-schema-change（Percona 工具）：创建新表 → 建立触发器同步增量数据 → 拷贝旧表数据 → 原子切换表名 → 删除旧表。期间业务无感知。(3) gh-ost（GitHub）：类似方案但基于 Binlog 同步，不使用触发器，对主库压力更小。最佳实践：加字段设默认值 NULL（避免全表更新），评估新字段对索引和查询的影响。",
          "difficulty": 3
        },
        {
          "id": "dd-008",
          "question": "索引选择性是什么？如何评估一个列是否适合建索引？",
          "answer": "索引选择性（Selectivity）：COUNT(DISTINCT col)/COUNT(*)，值越接近 1 说明列的区分度越高，索引过滤效果越好。经验值：选择性 > 0.1（区分 10% 以上）才有索引价值。评估方法：(1) 通过 SQL 计算选择性；(2) 查看实际查询的 WHERE 条件组合；(3) 考虑数据分布（如性别列只有男/女，选择性极低，不适合单独建索引）。不适合建索引的场景：低基数列、频繁更新的列（索引维护开销）、数据量小的表（全表扫描更快）、不用于查询条件的列。联合索引中，将选择性高的列放在前面。",
          "difficulty": 2
        },
        {
          "id": "dd-009",
          "question": "数据版本控制和并发更新冲突怎么处理？",
          "answer": "乐观锁（推荐）：版本号字段（version），UPDATE SET ... WHERE id=? AND version=?。更新失败（affected rows=0）说明被并发修改，应用层决定重试或提示用户。适用读多写少场景。悲观锁：SELECT ... FOR UPDATE 锁定记录，其他事务等待。适用写多冲突多的场景，但并发度低。CAS 机制：类似乐观锁，Compare And Swap。分布式场景：Redis 分布式锁 + 版本号双重保障。注意事项：乐观锁在高并发写场景下重试次数多，可设置最大重试次数 + 指数退避。MyBatis-Plus 的 @Version 注解可自动处理乐观锁逻辑。",
          "difficulty": 2
        }
      ]
    },
    {
      "id": "redis",
      "label": "Redis",
      "icon": "🔴",
      "description": "数据类型、缓存问题、分布式锁、集群",
      "source": "redis.md",
      "questions": [
        {
          "id": "rd-001",
          "question": "Redis 五种基础数据类型及适用场景？ZSet 为什么用跳表而不是红黑树？",
          "answer": "五种基础类型：String（缓存、计数器、分布式锁）、Hash（对象存储、购物车）、List（消息队列、最新列表）、Set（去重、交集/并集、标签）、ZSet（排行榜、延迟队列、滑动窗口限流）。ZSet 用跳表的原因：(1) 范围查询效率高（跳表 O(log n)，红黑树需要中序遍历）；(2) 实现简单（相比红黑树的旋转操作）；(3) 内存灵活（跳表节点可通过调整层数平衡内存和性能）；(4) 范围查询和插入性能均衡。跳表平均查找 O(log n)，最坏 O(n)。",
          "difficulty": 2
        },
        {
          "id": "rd-002",
          "question": "缓存穿透、缓存击穿、缓存雪崩分别是什么？如何解决？",
          "answer": "缓存穿透：查询不存在的数据，缓存和数据库都没有，每次请求打到数据库。解决：布隆过滤器（Bloom Filter）过滤不存在的 key、缓存空值（短时间过期）、参数校验。缓存击穿：热点 key 过期瞬间，大量并发请求同时打到数据库。解决：互斥锁（SETNX，只有一个线程查库并回写）、永不过期（逻辑过期，异步更新）、热点数据预热。缓存雪崩：大量 key 同时过期或 Redis 宕机，请求全部打到数据库。解决：过期时间加随机值、多级缓存、Redis 高可用（哨兵/集群）、熔断降级。",
          "difficulty": 2
        },
        {
          "id": "rd-003",
          "question": "Redis 分布式锁的实现方式？Redisson 的可重入锁和看门狗机制？",
          "answer": "基础实现：SET key value NX EX seconds，释放用 Lua 脚本（先比较值再删除，防止误删其他线程的锁）。Redisson 可重入锁：基于 Hash 结构，field 为线程标识，value 为重入次数。加锁 Lua 脚本判断 hash 中是否存在，存在则重入次数+1，不存在则设置并加过期时间。看门狗（Watchdog）：默认锁过期时间 30 秒，如果业务未执行完，看门狗每 10 秒（lockWatchdogTimeout/3）自动续期。只有未指定 leaseTime 时才启动看门狗。RedLock 算法在多个独立 Redis 实例上获取锁，但存在争议（Martin Kleppmann 的质疑），替代方案是 fencing token。",
          "difficulty": 3
        },
        {
          "id": "rd-004",
          "question": "Redis 的 RDB 和 AOF 持久化有什么区别？混合持久化是什么？",
          "answer": "RDB（Redis Database）：fork 子进程，利用 COW（写时复制）生成内存快照文件。优点：文件紧凑、恢复速度快。缺点：可能丢失最后一次快照后的数据，fork 时可能阻塞。AOF（Append Only File）：记录每个写命令到日志文件。同步策略：always（每条命令 fsync）、everysec（每秒 fsync，默认）、no（由 OS 决定）。优点：数据安全性高。缺点：文件体积大、恢复速度慢。混合持久化（Redis 4.0+）：RDB 格式写头部 + AOF 格式写增量，兼顾恢复速度和数据安全。AOF 重写（bgrewriteaof）：压缩 AOF 文件，只保留最终状态的命令。",
          "difficulty": 2
        },
        {
          "id": "rd-005",
          "question": "Redis 的主从复制、哨兵模式和 Cluster 模式各是什么？",
          "answer": "主从复制：全量同步（RDB + 增量缓冲区）和增量同步（Replication Backlog）。从库只读，主库负责写入。缺点：手动故障转移、单点写入瓶颈。哨兵模式（Sentinel）：监控主从节点，自动故障转移（选举新主库、通知客户端、配置更新）。主观下线（单个 Sentinel 判断）→ 客观下线（多数 Sentinel 确认）→ 故障转移。Cluster 模式：数据分片（16384 个槽位），每个节点负责部分槽位。Gossip 协议节点通信，客户端收到 MOVED/ASK 重定向。支持水平扩展，多主写入。",
          "difficulty": 3
        },
        {
          "id": "rd-006",
          "question": "Redis 的 Pipeline 和 Lua 脚本各有什么用？BigKey 怎么处理？",
          "answer": "Pipeline：将多条命令打包一次性发送，减少网络 RTT（Round-Trip Time）。适合批量操作场景。注意：Pipeline 中的命令不是原子执行。Lua 脚本：在 Redis 服务端原子执行多条命令，保证原子性。用于分布式锁释放、库存扣减等需要原子操作的场景。注意：Lua 脚本执行期间阻塞其他命令。BigKey 处理：检测（redis-cli --bigkeys、redis-rdb-tools）、拆分（Hash 拆分成多个小 Hash）、删除用 UNLINK（异步删除，不阻塞主线程）。HotKey 处理：本地缓存 + 热点 key 分散（加随机后缀）。",
          "difficulty": 2
        },
        {
          "id": "rd-007",
          "question": "Redis 过期策略和内存淘汰策略分别是什么？",
          "answer": "过期策略（如何删除过期 key）：(1) 惰性删除：访问 key 时检查是否过期，过期则删除；(2) 定期删除：每 100ms 随机抽取一批 key 检查并删除过期 key，限制执行时长避免卡顿。Redis 同时使用两种策略。内存淘汰策略（内存满时如何处理新写入）：volatile-lru（从设置了过期时间的 key 中淘汰最近最少使用）、allkeys-lru（从所有 key 中淘汰最近最少使用，默认推荐）、volatile-lfu/allkeys-lfu（最少使用频率）、volatile-random/allkeys-random（随机淘汰）、volatile-ttl（淘汰最近过期的）、noeviction（不淘汰，写入报错）。生产推荐：allkeys-lru 或 allkeys-lfu。",
          "difficulty": 2
        },
        {
          "id": "rd-008",
          "question": "缓存与数据库一致性如何保证？",
          "answer": "常见方案：(1) Cache Aside（旁路缓存，最常用）：读：先查缓存，未命中则查数据库并回写缓存；写：先更新数据库，再删除缓存。删除缓存而非更新缓存（避免并发写导致数据不一致）。(2) 延迟双删：更新数据库前删除缓存 → 更新数据库 → 延迟 N 毫秒后再次删除缓存（防止并发读回写旧数据）。(3) Canal 监听 Binlog：数据库更新后 Canal 捕获变更事件，异步删除/更新缓存，最终一致性。(4) 消息队列：数据库更新后发 MQ 消息，消费者删除缓存。注意：极端并发下 Cache Aside 仍可能出现短暂不一致（数据库更新完但缓存删除失败），可通过重试 + 告警保障。",
          "difficulty": 3
        },
        {
          "id": "rd-009",
          "question": "Redis 的 Bitmap、HyperLogLog、Stream 分别适用于什么场景？",
          "answer": "Bitmap（位图）：基于 String 类型，每个 bit 位表示一个元素的状态（0/1）。适合：用户签到（按天设 bit）、在线状态、活跃统计。内存极小（1 亿用户签到仅占约 12MB）。HyperLogLog：基数估算算法，用于 UV（独立访客）统计。标准误差 0.81%，12KB 固定内存可统计 2^64 个元素。不可获取具体元素，只能获取去重后的数量。Stream（Redis 5.0+）：消息队列数据类型，支持持久化、消费者组、消息确认、阻塞读取。适合：轻量级消息队列、聊天消息、实时通知。对比 List：Stream 支持多消费者组和消息确认，更可靠。",
          "difficulty": 2
        },
        {
          "id": "rd-010",
          "question": "Redis Cluster 的 16384 个槽位是怎么分配的？客户端如何定位？",
          "answer": "槽位分配：Redis Cluster 将 16384 个哈希槽（CRC16(key) % 16384）分配给各节点，每个节点负责一部分槽位。集群创建时自动平均分配，也可手动迁移。客户端定位：客户端收到 MOVED/ASK 重定向响应时更新本地槽位映射缓存。后续请求直接发送到正确节点（本地缓存）。节点间通过 Gossip 协议交换状态信息（节点上下线、槽位变更）。新增节点：从已有节点迁移部分槽位到新节点。节点故障：其他节点标记为 PFAIL（主观下线），超过半数 master 标记为 FAIL（客观下线），从节点自动提升为主节点。",
          "difficulty": 3
        }
      ]
    },
    {
      "id": "mq",
      "label": "消息队列",
      "icon": "📨",
      "description": "消息模型、可靠性、积压处理、MQ对比",
      "source": "mq.md",
      "questions": [
        {
          "id": "mq-001",
          "question": "消息队列的三种投递语义？如何实现幂等消费？",
          "answer": "At Most Once（至多一次）：可能丢消息，生产者发送后不确认，性能最高。At Least Once（至少一次）：可能重复，MQ 默认语义，消费失败会重试，消费端必须幂等。Exactly Once（恰好一次）：不丢不重，Kafka 通过事务 + 幂等 Producer 实现，RocketMQ 通过事务消息实现。幂等消费实现：(1) 唯一键去重（Redis SETNX 或数据库唯一约束）；(2) 业务状态机判断（如订单状态只能从「待支付」→「已支付」）；(3) 全局唯一消息 ID + 消费记录表。",
          "difficulty": 2
        },
        {
          "id": "mq-002",
          "question": "如何保证消息不丢失？生产端和消费端分别怎么处理？",
          "answer": "生产端：Kafka 设置 acks=all（所有副本确认）+ retries > 0；RabbitMQ 开启 Publisher Confirm（回调确认）+ 发送失败重试；RocketMQ 同步发送。MQ 端：消息持久化到磁盘（Kafka 的 log.segment.bytes + flush 消息，RocketMQ 的同步刷盘/异步刷盘）+ 副本机制（Kafka ISR，RocketMQ 同步复制）。消费端：手动 ACK（关闭自动确认），处理完成后再确认。死信队列（DLQ）：消费重试达到上限后转入死信队列，人工介入处理。",
          "difficulty": 2
        },
        {
          "id": "mq-003",
          "question": "消息积压了 100 万条怎么处理？",
          "answer": "应急处理：(1) 排查积压原因（消费者宕机、处理变慢、下游超时、突发流量）；(2) 紧急扩容消费者实例（增加 Partition 对应的消费者数量）；(3) 临时消费者：绕过耗时逻辑（如发 MQ），先将消息落库保存，后续慢慢处理；(4) 如果消费者无问题，检查下游服务是否正常。长期预防：(1) 消费监控告警（Lag 阈值监控）；(2) 容量预估（峰值 QPS 评估消费者数量）；(3) 消费幂等保证安全扩容（避免重复消费造成业务问题）；(4) 削峰填谷：MQ 作为缓冲层保护下游系统。",
          "difficulty": 2
        },
        {
          "id": "mq-004",
          "question": "Kafka、RocketMQ、RabbitMQ 的对比和选型？",
          "answer": "Kafka：高吞吐（百万级 TPS）、顺序磁盘写 + 零拷贝、Pull 模式、分区机制、适合日志收集、事件流、大数据。RocketMQ：事务消息、延迟消息（18个级别）、消息过滤（Tag/SQL92）、Push/Pull 模式、适合电商、金融场景（订单、支付）。RabbitMQ：AMQP 协议、Exchange 路由灵活（direct/topic/fanout）、Push 模式、适合任务分发、RPC。选型维度：吞吐量（Kafka > RocketMQ > RabbitMQ）、延迟（RabbitMQ 微秒级）、消息可靠性（RocketMQ 事务消息）、运维复杂度（RabbitMQ 最简单）、社区生态（Kafka 最活跃）。",
          "difficulty": 2
        },
        {
          "id": "mq-005",
          "question": "消费者 Rebalance 期间消息怎么处理？如何避免重复消费？",
          "answer": "Rebalance：消费者组成员变化（上下线、扩缩容）时触发，分区/队列重新分配。期间：(1) Kafka：正在处理的消息不会被重复投递（消费者通过心跳维持会话），但 Rebalance 前已拉取未处理完的消息可能在新分配后由其他消费者重新拉取（需幂等）。(2) RocketMQ：Rebalance 时会触发消费进度提交，已拉取未 ACK 的消息在新消费者上重新投递。避免重复消费：幂等设计（唯一键、去重表、状态机）、合理的消费进度提交策略（处理完再提交 offset）。",
          "difficulty": 3
        },
        {
          "id": "mq-006",
          "question": "消息顺序性如何保证？什么场景需要全局有序？",
          "answer": "分区有序：Kafka 同一 Partition 内消息有序（FIFO），RocketMQ 同一 MessageQueue 内有序。保证方式：相同 Key 的消息路由到同一分区（如订单ID作为Key）。全局有序：需要所有消息都发到同一个分区（单 Partition），吞吐量大幅下降。实际业务中很少需要全局有序，大部分场景只需分区有序即可。注意：(1) 多消费者消费同一分区时仍需单线程消费才能保证有序；(2) 消费失败重试可能导致后续消息阻塞（需注意重试策略）。典型场景：订单状态变更（同一订单的事件有序）。",
          "difficulty": 2
        },
        {
          "id": "mq-007",
          "question": "RocketMQ 事务消息的实现原理？",
          "answer": "事务消息流程：(1) 生产者发送半消息（Half Message）到 Broker，消息对消费者不可见；(2) Broker 存储半消息并返回确认；(3) 生产者执行本地事务（如数据库操作）；(4) 根据本地事务结果发送 Commit（消息投递给消费者）或 Rollback（删除半消息）。异常处理：如果生产者未发送 Commit/Rollback（如宕机），Broker 会主动回查生产者的本地事务状态（Check 事务回查接口），根据结果决定消息的最终状态。回查最多 15 次，超过则回滚。适用场景：分布式事务（如订单创建后发消息扣库存），比 2PC/TCC 更轻量。",
          "difficulty": 3
        },
        {
          "id": "mq-008",
          "question": "Kafka 的 ISR 机制是什么？副本同步策略？",
          "answer": "ISR（In-Sync Replicas）：与 Leader 保持同步的副本集合。Follower 从 Leader 拉取数据，如果落后太多（延迟超过 replica.lag.time.max.ms），则被踢出 ISR。Leader 从 ISR 中选举。acks 参数：acks=0（不等确认，最快但可能丢）、acks=1（等 Leader 确认）、acks=all/-1（等 ISR 中所有副本确认，最安全）。LEO（Log End Offset）：每个副本的日志末尾偏移量。HW（High Watermark）：所有 ISR 副本都确认的最大偏移量，消费者只能看到 HW 之前的消息。Unclean Leader 选举：当 ISR 为空时，允许非 ISR 副本成为 Leader（可能丢数据），由 unclean.leader.election.enable 控制。",
          "difficulty": 3
        },
        {
          "id": "mq-009",
          "question": "如何设计消息的全链路追踪？消息丢失的排查思路？",
          "answer": "全链路追踪：(1) 每条消息携带 TraceId（从请求入口透传到 MQ 消息）；(2) 生产者发送时记录日志（消息ID、发送时间、Topic/Queue）；(3) Broker 投递确认（Kafka acks、RabbitMQ Publisher Confirm）；(4) 消费者消费时记录日志（消费时间、处理结果）；(5) 分布式追踪系统集成（SkyWalking、Jaeger 的 MQ 插件自动埋点）。消息丢失排查思路：(1) 检查生产者发送是否成功（确认回调、日志）；(2) 检查 Broker 消息是否持久化（是否刷盘、副本数）；(3) 检查消费者是否 ACK（手动确认是否执行）；(4) 检查消费重试和死信队列；(5) 端到端消息对账（生产者发送数量 vs 消费者消费数量）。",
          "difficulty": 2
        },
        {
          "id": "mq-010",
          "question": "消息队列如何实现延迟消息？",
          "answer": "方案对比：(1) RocketMQ：原生支持 18 个延迟级别（1s/5s/10s/30s/1m/2m/3m/4m/5m/6m/7m/8m/9m/10m/20m/30m/1h/2h），修改 message.delayTimeLevel。开源版不支持任意时间。(2) RabbitMQ：通过死信队列 + TTL 实现。消息设过期时间 → 进入死信队列 → 死信队列消费者处理。支持任意延迟时间，但不精确（TTL 检查机制）。(3) Kafka：原生不支持，需自己实现（时间轮 + 定时扫描或外部定时任务）。(4) Redis：ZSet score=执行时间戳 + 定时轮询。适合中小规模。选型：RocketMQ 延迟消息最成熟，RabbitMQ 灵活但需额外队列。",
          "difficulty": 2
        }
      ]
    },
    {
      "id": "distributed",
      "label": "分布式系统",
      "icon": "🌐",
      "description": "CAP、共识算法、分布式事务、ID",
      "source": "distributed.md",
      "questions": [
        {
          "id": "ds-001",
          "question": "CAP 定理是什么？BASE 理论与 ACID 的区别？",
          "answer": "CAP：一致性（Consistency）、可用性（Availability）、分区容错性（Partition tolerance），三者不可兼得，网络分区（P）必然存在时只能在 C 和 A 之间取舍。CP：保证一致性牺牲可用性（如 ZooKeeper、etcd）；AP：保证可用性牺牲一致性（如 Eureka、Cassandra）。BASE：基本可用（Basically Available）、软状态（Soft State）、最终一致性（Eventual Consistency），是对 CAP 中 AP 的延伸，与 ACID 的强一致性对立。BASE 是大型分布式系统的实践指导思想，如缓存与数据库的最终一致性。",
          "difficulty": 2
        },
        {
          "id": "ds-002",
          "question": "Raft 共识算法的核心流程？和 Paxos 有什么区别？",
          "answer": "Raft 角色和流程：Leader（处理所有写请求）、Follower（被动跟随）、Candidate（选举状态）。Leader 选举：Follower 超时未收到心跳 → 转为 Candidate → 请求投票（任期+日志对比）→ 获得多数票成为 Leader。日志复制：客户端发送写请求 → Leader 追加到本地日志 → 复制到多数 Follower → 提交并返回客户端。安全性：Leader 只追加日志、日志只从 Leader 流向 Follower、通过任期和日志对比保证选举出的 Leader 包含所有已提交日志。Raft vs Paxos：Raft 为可理解性设计，分工明确（选举、日志复制、安全性分离）；Paxos 更通用但难以理解和实现。",
          "difficulty": 3
        },
        {
          "id": "ds-003",
          "question": "分布式事务的 2PC、TCC、SAGA 模式各是什么？",
          "answer": "2PC（Two-Phase Commit）：协调者发起 Prepare → 所有参与者投票 Yes → 协调者发 Commit。缺点：同步阻塞、单点故障（协调者挂了参与者一直锁着）。TCC（Try-Confirm-Cancel）：Try 阶段预留资源、Confirm 阶段确认提交、Cancel 阶段回滚释放。需处理空回滚（Try 未执行但收到 Cancel）和幂等。SAGA：长事务拆分为多个本地事务，每个事务有对应的补偿操作。正向执行失败则反向补偿。适合长事务（如跨服务订单流程）。Seata AT 模式：一阶段提交 + 全局锁 + 二阶段回滚（undo_log），对业务无侵入。",
          "difficulty": 3
        },
        {
          "id": "ds-004",
          "question": "雪花算法（Snowflake）的结构？时钟回拨问题如何解决？",
          "answer": "雪花算法生成 64 位 Long 型 ID：1 位符号位（0）+ 41 位时间戳（毫秒级，可用约 69 年）+ 10 位机器标识（workerId，1024 个节点）+ 12 位序列号（同一毫秒内 4096 个 ID）。优点：趋势递增、不依赖数据库、分布式唯一。时钟回拨问题：NTP 校时可能导致系统时钟回拨，同一毫秒可能生成重复 ID。解决：(1) 回拨时间短则等待；（2）回拨时间长则抛异常或切换 workerId；(3) 使用历史最大时间戳判断。替代方案：号段模式（Leaf，数据库分配号段，用完再取，减少数据库压力）、UUID（无序，占用空间大，索引性能差）。",
          "difficulty": 2
        },
        {
          "id": "ds-005",
          "question": "一致性哈希是什么？虚拟节点解决了什么问题？",
          "answer": "一致性哈希：将整个哈希值空间组织成一个虚拟的圆环（0~2^32-1），节点和 key 都映射到环上。顺时针方向，key 存储到最近的节点。节点增减时只影响相邻节点之间的数据（最小化数据迁移），优于传统哈希取模（增减节点需全量迁移）。虚拟节点：每个真实节点映射到环上的多个虚拟位置（如 150 个），使数据分布更均匀，避免数据倾斜。节点越多、虚拟节点越多，分布越均匀。DHT（分布式哈希表）的基础理论。",
          "difficulty": 2
        },
        {
          "id": "ds-006",
          "question": "Redis 分布式锁和 ZooKeeper 分布式锁怎么选？各自的优缺点？",
          "answer": "Redis 分布式锁：SET NX EX + Lua 释放，性能高（单次操作微秒级），实现简单。缺点：Redis Cluster 主从切换时可能丢失锁（异步复制），RedLock 尝试解决但存在争议。适合：高性能要求、锁竞争不激烈、允许极端情况丢锁的场景。ZooKeeper 分布式锁：临时顺序节点 + Watch 机制，CP 模型保证锁的可靠性。优点：锁可靠（ZAB 协议保证），支持公平锁（按创建顺序获取）、可重入锁。缺点：性能低于 Redis（需要多次网络通信），ZK 集群运维复杂。适合：锁可靠性要求极高（如金融场景）、需要公平排队的场景。选型：性能优先 Redis，可靠性优先 ZK。",
          "difficulty": 2
        },
        {
          "id": "ds-007",
          "question": "Seata AT 模式的工作原理？",
          "answer": "Seata AT 模式（Automatic Transaction）对业务无侵入，分为两阶段：一阶段：业务 SQL 执行前拦截，保存数据的前镜像（修改前的值）到 undo_log；执行业务 SQL；保存后镜像（修改后的值）到 undo_log；生成全局锁并提交本地事务（释放数据库锁，但持有 Seata 全局锁）。二阶段提交：异步清理 undo_log。二阶段回滚：根据 undo_log 的前镜像恢复数据，然后删除 undo_log。全局锁：保证同一全局事务内，其他事务不能修改相同数据。注意：全局锁持有时间过长会影响并发性能，长事务需谨慎。",
          "difficulty": 3
        },
        {
          "id": "ds-008",
          "question": "API 网关的核心职责？HTTP 和 RPC 的区别？",
          "answer": "网关核心职责：(1) 路由转发（请求路由到后端服务）；(2) 鉴权（统一认证授权）；(3) 限流熔断（保护后端服务）；(4) 日志监控（请求链路追踪）；(5) 协议转换（HTTP → gRPC/Dubbo）；(6) 灰度发布（按规则分流）。常见网关：Spring Cloud Gateway、Nginx、Kong。HTTP vs RPC：HTTP（RESTful）：基于文本/JSON，可读性好，跨语言友好，适合对外 API。RPC（Dubbo/gRPC）：基于二进制序列化，性能高（序列化开销小、连接复用），适合微服务内部调用。选型：对外用 HTTP，内部高频调用用 RPC。",
          "difficulty": 2
        },
        {
          "id": "ds-009",
          "question": "分布式系统的服务注册与发现怎么做？Nacos、Eureka、Consul 的区别？",
          "answer": "服务注册：服务启动时将自身信息（IP、端口、服务名、健康状态）注册到注册中心。服务发现：消费者从注册中心获取服务提供者列表，本地缓存并定期刷新。Nacos：支持 AP/CP 切换、配置管理 + 服务发现一体化、支持服务分组和权重路由。Eureka：AP 模型，节点间最终一致性，已停止维护（Spring Cloud Alibaba 推荐用 Nacos）。Consul：CP 模型（Raft），支持健康检查（TCP/HTTP/Script）、KV 存储、多数据中心。选型：Spring Cloud 生态用 Nacos，Go/多云用 Consul。",
          "difficulty": 2
        }
      ]
    },
    {
      "id": "high-availability",
      "label": "高可用",
      "icon": "🛡️",
      "description": "限流、熔断、降级、容灾、可观测性",
      "source": "high-availability.md",
      "questions": [
        {
          "id": "ha-001",
          "question": "四种限流算法的原理和优缺点？",
          "answer": "固定窗口：固定时间窗口内计数，窗口切换时可能有临界突刺（窗口边界2倍流量）。滑动窗口：将窗口细分为小格子，更平滑，但实现稍复杂。令牌桶（Token Bucket）：以固定速率生成令牌放入桶中，请求需获取令牌，桶满则丢弃令牌。允许一定程度的突发流量（桶中积累的令牌），匀速放行。漏桶（Leaky Bucket）：请求进入桶中，以固定速率流出，削峰填谷。不允许突发，输出恒定速率。分布式限流：Redis Lua 原子计数 + 滑动窗口、Sentinel 集群限流模式。",
          "difficulty": 2
        },
        {
          "id": "ha-002",
          "question": "熔断器模式的三种状态？降级策略有哪些？",
          "answer": "熔断器状态机：Closed（正常状态，请求正常通过，统计失败率/慢调用率）→ Open（熔断打开，所有请求直接拒绝/走降级逻辑，不调用下游）→ Half-Open（半开状态，放行少量探测请求，成功则切回 Closed，失败则切回 Open）。触发条件：失败率超过阈值或慢调用率超过阈值（如 50% 失败率或 60% 慢调用）。降级策略：(1) 返回缓存数据/默认值；(2) 返回简化结果；(3) 关闭非核心功能；(4) 手动开关（配置中心动态控制）。级联故障预防：超时设置（connect/read）、重试次数与退避策略（指数退避）、舱壁隔离（线程池/信号量隔离不同下游）。",
          "difficulty": 2
        },
        {
          "id": "ha-003",
          "question": "SLA、SLO、SLI 的关系？可观测性三大支柱？",
          "answer": "SLI（Service Level Indicator）：服务质量指标，如延迟（P99）、错误率、可用性、吞吐量。SLO（Service Level Objective）：服务目标，如「99.9% 的请求在 200ms 内返回」。SLA（Service Level Agreement）：服务等级协议，是合同承诺，达不到则有赔偿（如服务 credits）。关系：基于 SLI 设定 SLO，SLO 写入 SLA。三大支柱：Metrics（指标，如 Prometheus + Grafana）、Logging（日志，如 ELK/Loki）、Tracing（链路追踪，如 Jaeger/SkyWalking）。告警设计：分级（P0-P3）、收敛（抑制/聚合/路由）、OnCall 轮值与升级机制。",
          "difficulty": 2
        },
        {
          "id": "ha-004",
          "question": "流量突增 10 倍，系统哪里先扛不住？怎么应急？",
          "answer": "瓶颈分析顺序：前端/CDN → 网关（限流）→ 应用服务（线程池打满）→ 数据库（连接池满/慢查询）→ 缓存（穿透到数据库）→ MQ（消息积压）。应急措施：(1) 网关限流/熔断，拒绝超出容量的请求；(2) 静态化 + CDN 缓存，减少后端压力；(3) 降级非核心功能（如推荐、评论、搜索）；(4) 扩容应用服务（K8s HPA 或手动加实例）；(5) 数据库读写分离、加缓存；(6) 降级为返回缓存数据或默认值。关键：有预案、有演练、有监控告警。",
          "difficulty": 2
        },
        {
          "id": "ha-005",
          "question": "脑裂是什么？如何防止脑裂？",
          "answer": "脑裂（Split Brain）：主备切换时，网络分区导致两个节点都认为自己是主节点，造成双主写入，数据不一致。场景：主节点与备用节点之间网络断开，备用节点认为主节点宕机而接管，但主节点实际还在运行。防护措施：(1) Fencing（STONITH - Shoot The Other Node In The Head）：通过硬件或软件机制强制关停旧主节点；(2) 仲裁节点：需要多数节点（Quorum）同意才能切换，保证最多一个主节点；(3) Lease 锁：租约过期机制，持有租约的节点才有写权限；(4) ZooKeeper 临时节点：会话超时后节点自动失效。",
          "difficulty": 3
        },
        {
          "id": "ha-006",
          "question": "混沌工程是什么？如何在团队中推行？",
          "answer": "混沌工程（Chaos Engineering）：通过主动注入故障（网络延迟、节点宕机、依赖不可用、磁盘满等）来验证系统韧性的工程实践。核心理念：假设故障必然发生，提前发现和修复脆弱点。工具：Chaos Mesh（Kubernetes 环境）、LitmusChaos、Gremlin、阿里云 AHAS Chaos。实施步骤：(1) 定义稳态假设（正常状态下系统的行为指标）；(2) 设计最小化实验（注入一个故障）；(3) 观察系统反应；(4) 验证是否偏离稳态。推行策略：从非生产环境开始、先做读副本故障、建立故障分级（P0-P3）、与发布流程集成（上线前必过混沌测试）。注意：必须有多人知晓实验、有回滚预案、避开业务高峰。",
          "difficulty": 2
        },
        {
          "id": "ha-007",
          "question": "异地多活的架构设计？单元化是什么？",
          "answer": "异地多活：在多个地理区域部署完整的应用和数据库，每个区域都能独立提供服务。挑战：跨区域数据同步延迟（如北京-上海约 20ms）、数据一致性、路由规则。单元化架构：按用户维度将请求路由到固定单元（如用户ID % 3 → 单元A/B/C），每个单元拥有完整的业务逻辑和数据，单元间不需要跨单元调用。数据同步：同城双活用数据库双向同步（延迟低），异地用 Canal/Otter 基于 Binlog 异步同步。路由层：DNS 就近路由 + 网关根据用户 ID 路由到所属单元。容灾演练：定期模拟单机房故障，验证自动切换和数据一致性。成本高，通常只有大型互联网公司采用。",
          "difficulty": 3
        },
        {
          "id": "ha-008",
          "question": "压测方法论？如何评估系统容量？",
          "answer": "压测流程：基准压测（单接口，了解单机极限）→ 阶梯加压（逐步增加并发，找出拐点）→ 极限探测（超过拐点观察系统行为）→ 长时间稳定性测试（Stress Test，运行 24h+ 观察内存泄漏等）。关键指标：QPS/TPS（吞吐量）、响应时间（P50/P90/P99/P999）、错误率。容量评估：(1) 业务预估（DAU、峰值 QPS、读写比）；(2) 单机压测获得基准数据；(3) 根据峰值 QPS / 单机 QPS = 需要的实例数，加 30~50% 冗余。注意：关注长尾延迟（P99 而非均值），模拟真实业务场景（含缓存、MQ），生产环境压测需做好降级预案。",
          "difficulty": 2
        },
        {
          "id": "ha-009",
          "question": "错误预算是什么？如何用于指导发布节奏？",
          "answer": "错误预算（Error Budget）= 1 - SLO 目标。如 SLO 99.9% 可用性，则错误预算 = 0.1%（每月约 43 分钟的不可用时间）。消耗预算：每次故障或发布导致的不可用时间从预算中扣除。预算用完：停止发布新功能，全力做稳定性改进（修复 bug、加固监控、重写脆弱模块），直到预算恢复。指导发布：(1) 预算充足时正常发布节奏；(2) 预算紧张时降低发布频率，增加灰度比例；(3) 预算耗尽时暂停功能发布，只允许热修复。错误预算让 SRE 和产品团队在「稳定性」和「功能迭代」之间有共同的语言和明确的决策依据。",
          "difficulty": 2
        }
      ]
    },
    {
      "id": "design-pattern",
      "label": "设计模式",
      "icon": "🧩",
      "description": "创建型、结构型、行为型模式",
      "source": "design-pattern.md",
      "questions": [
        {
          "id": "dp-001",
          "question": "单例模式的 5 种实现方式？DCL 为何要 volatile？枚举如何防反射/序列化？",
          "answer": "5 种实现：(1) 饿汉式（类加载时创建，线程安全但不支持延迟加载）；(2) 懒汉式（synchronized 方法，线程安全但性能差）；(3) DCL 双重检查锁（volatile + synchronized，推荐）；(4) 静态内部类（Holder 模式，利用类加载机制保证线程安全，推荐）；(5) 枚举（Effective Java 推荐，天然防反射和序列化破坏）。DCL 需要 volatile：因为对象创建不是原子操作（分配内存→初始化→引用赋值），JVM 指令重排序可能导致其他线程获取到未初始化的对象。volatile 禁止重排序。枚举防反射：反射创建枚举实例时抛 IllegalArgumentException；防序列化：枚举的反序列化直接返回已有枚举实例。",
          "difficulty": 3
        },
        {
          "id": "dp-002",
          "question": "工厂模式的递进关系？与建造者模式有什么区别？",
          "answer": "工厂模式递进：简单工厂（一个工厂类包含所有创建逻辑，违反开闭原则）→ 工厂方法（每个产品一个工厂，符合开闭原则但类数量增加）→ 抽象工厂（一组相关产品的工厂族，适合产品族场景）。开闭原则体现：简单工厂修改工厂类（不符合）、工厂方法增加工厂类（符合）、抽象工厂增加工厂接口实现（符合）。工厂模式 vs 建造者模式：工厂模式关注创建单个对象，建造者模式关注创建复杂对象（多步骤、多参数）。建造者模式强调链式调用和逐步构建（Lombok @Builder），适合构造函数参数过多的场景。",
          "difficulty": 2
        },
        {
          "id": "dp-003",
          "question": "JDK 动态代理和 CGLIB 代理的区别？Spring AOP 默认选哪个？",
          "answer": "JDK 动态代理：基于接口，通过 Proxy.newProxyInstance 创建代理对象，InvocationHandler 处理方法调用。目标类必须实现接口。CGLIB：基于继承，通过字节码技术生成目标类的子类，MethodInterceptor 拦截方法调用。不能代理 final 类和方法。性能：CGLIB 创建代理慢（生成字节码）但调用快，JDK 反之。Spring AOP 默认策略：有接口用 JDK 代理，无接口用 CGLIB（Spring Boot 2.x 默认都用 CGLIB，spring.aop.proxy-target-class=true）。",
          "difficulty": 2
        },
        {
          "id": "dp-004",
          "question": "策略模式如何消除 if-else？责任链模式的典型应用？",
          "answer": "策略模式：将不同策略封装为独立类，实现统一接口，通过工厂或 Spring 注入实现策略路由。消除 if-else 的方式：Map<StrategyType, Strategy> strategyMap 存储策略，根据类型直接获取对应策略执行。适合多种算法/规则可互换的场景（如支付方式、促销计算、审批流程）。责任链模式：将请求沿链传递，每个处理器决定处理或传递给下一个。典型应用：Servlet Filter（过滤器链）、Spring Interceptor（拦截器链）、Netty Pipeline（ChannelPipeline）、审批流程（多级审批）、日志处理（不同级别日志不同处理）。与策略模式的区别：策略是一选一，责任链是多选多（链上多个处理器可能都处理）。",
          "difficulty": 2
        },
        {
          "id": "dp-005",
          "question": "观察者模式和发布订阅模式有什么区别？Spring 事件机制用的是哪个？",
          "answer": "观察者模式：Subject 直接通知 Observer，两者有直接依赖关系（Subject 维护 Observer 列表）。发布订阅模式：Publisher 和 Subscriber 通过 Event Broker/Message Broker 中介解耦，彼此不知道对方的存在。区别：耦合度（观察者模式比发布订阅紧耦合）、通知方式（观察者同步调用，发布订阅可异步）。Spring 事件机制本质是观察者模式（ApplicationEventPublisher → @EventListener），但支持 @Async 异步执行，具备发布订阅的特性。实际 MQ（Kafka/RocketMQ）是标准的发布订阅模式。",
          "difficulty": 2
        },
        {
          "id": "dp-006",
          "question": "模板方法模式的核心思想？Spring 中哪些地方用到了？",
          "answer": "模板方法模式：在父类中定义算法的骨架（一系列步骤），将具体步骤延迟到子类实现。核心：不变的部分在父类（算法骨架），变化的部分在子类（具体实现），符合开闭原则。Spring 中的使用：(1) AbstractApplicationContext#refresh()：定义 IoC 容器初始化的 12 步流程，子类可重写特定步骤；(2) JdbcTemplate：定义数据库操作的骨架（获取连接→执行SQL→处理结果→释放连接），用户只需提供 SQL 和 RowMapper；(3) RestTemplate、RedisTemplate 同理。MyBatis 的 BaseExecutor 也是模板方法模式（子类 SimpleExecutor/BatchExecutor/ReuseExecutor 实现具体执行逻辑）。",
          "difficulty": 2
        },
        {
          "id": "dp-007",
          "question": "装饰器模式和代理模式有什么区别？",
          "answer": "装饰器模式：动态给对象添加额外职责，关注「增强功能」，用户需主动创建装饰器链。代理模式：控制对对象的访问，关注「访问控制」，代理对象替被代理对象执行操作。核心区别：(1) 目的不同：装饰器增加功能，代理控制访问；(2) 创建方式：装饰器由客户端主动组合，代理通常由框架自动创建（AOP）；(3) 关注点：装饰器关注「能做什么更多的事」，代理关注「谁能访问和怎么做」。Java I/O 是经典装饰器模式（InputStream → BufferedInputStream → DataInputStream）。Spring AOP 是代理模式（JDK/CGLIB 生成代理对象，在方法前后插入通知逻辑）。",
          "difficulty": 2
        },
        {
          "id": "dp-008",
          "question": "SOLID 原则分别是什么？项目中如何应用？",
          "answer": "S - 单一职责（SRP）：一个类只负责一个职责，修改原因只有一个。如 UserController 只处理请求转发，不包含业务逻辑。O - 开闭原则（OCP）：对扩展开放，对修改关闭。通过策略模式、模板方法实现。L - 里氏替换（LSP）：子类能替换父类使用而不影响正确性。子类不应重写父类的非抽象方法。I - 接口隔离（ISP）：接口应小而专一，不应强迫依赖不需要的方法。如拆分大接口为多个小接口。D - 依赖倒置（DIP）：依赖抽象而非具体实现。面向接口编程，通过 DI 容器注入依赖。实践：Spring 的 Bean 注入就是 DIP 的体现；策略模式 + 工厂模式体现 OCP。",
          "difficulty": 2
        }
      ]
    },
    {
      "id": "network-os",
      "label": "网络与 OS",
      "icon": "💻",
      "description": "TCP/IP、HTTP、并发模型、IO",
      "source": "network-os.md",
      "questions": [
        {
          "id": "nw-001",
          "question": "TCP 三次握手和四次挥手的过程？TIME_WAIT 状态的意义？",
          "answer": "三次握手：客户端 SYN（seq=x）→ 服务端 SYN+ACK（seq=y, ack=x+1）→ 客户端 ACK（ack=y+1）。目的：确认双方收发能力正常。四次挥手：主动方 FIN → 被动方 ACK → 被动方 FIN → 主动方 ACK。TIME_WAIT：主动关闭方最后发送 ACK 后进入此状态，持续 2MSL（最大报文生存时间）。意义：(1) 确保最后一个 ACK 能到达被动方（如丢失则被动方会重发 FIN）；(2) 等待网络中残留报文过期（避免新连接收到旧连接的报文）。优化：tcp_tw_reuse（允许复用 TIME_WAIT 连接）、调整 MSL。",
          "difficulty": 2
        },
        {
          "id": "nw-002",
          "question": "粘包和拆包的原因？如何解决？",
          "answer": "粘包：发送方多个小包合并成一个大包发送（Nagle 算法优化）。拆包：接收方缓冲区不够，一个大包被拆成多个包接收。根本原因：TCP 是面向字节流的协议，不维护消息边界。解决：(1) 固定包长：每个消息固定长度，不足补齐（浪费带宽）；(2) 分隔符：消息之间用特殊分隔符（如 \\r\\n），但消息体不能包含分隔符；(3) 长度字段：消息头包含消息体长度字段（最常用，如 Netty 的 LengthFieldBasedFrameDecoder）。应用层协议设计必须处理消息边界。",
          "difficulty": 2
        },
        {
          "id": "nw-003",
          "question": "HTTP/1.1 和 HTTP/2 的主要区别？HTTPS 握手流程？",
          "answer": "HTTP/2 改进：(1) 多路复用（一个 TCP 连接上并行多个请求/响应，解决 HTTP/1.1 的队头阻塞）；(2) 头部压缩（HPACK 算法）；(3) 二进制分帧（替代文本格式）；(4) 服务器推送（Server Push）。注意：HTTP/2 多路复用在 TCP 层仍有队头阻塞（丢包导致所有流等待），HTTP/3（QUIC 协议）改为基于 UDP 解决此问题。HTTPS 握手：TCP 三次握手 → Client Hello（支持的加密套件、随机数）→ Server Hello + 证书 + 服务器随机数 → 客户端验证证书 → 客户端生成预主密钥，用服务器公钥加密发送 → 双方基于预主密钥生成会话密钥 → 对称加密通信。会话复用：Session ID 或 Session Ticket 减少握手次数。",
          "difficulty": 2
        },
        {
          "id": "nw-004",
          "question": "零拷贝、mmap、sendfile 各自的原理和适用场景？",
          "answer": "零拷贝（Zero-Copy）：减少数据在用户态和内核态之间的拷贝次数。传统方式：4 次拷贝（磁盘→内核缓冲→用户缓冲→Socket 缓冲→网卡）。mmap：将文件映射到内存地址空间，用户态直接读写内核缓冲区，减少一次拷贝（3 次拷贝）。适合文件随机读写。sendfile：数据直接在内核态从文件缓冲区到 Socket 缓冲区（2 次拷贝，配合 DMA Scatter/Gather 可进一步减少到 1 次）。适合静态文件传输（Nginx、Kafka）。Java 中：FileChannel.transferTo() 底层调用 sendfile。",
          "difficulty": 3
        },
        {
          "id": "nw-005",
          "question": "Reactor 模型是什么？和同步阻塞模型有什么区别？",
          "answer": "Reactor 模式：基于 I/O 多路复用（epoll/kqueue/select），单线程或少量线程处理大量连接。三种变体：单 Reactor 单线程（Redis）、单 Reactor 多线程（Netty 可以配置）、主从 Reactor 多线程（Netty 默认，Boss Group 负责 Accept，Worker Group 负责 I/O 读写和业务处理）。同步阻塞模型（BIO）：每个连接一个线程，线程在 I/O 等待时被阻塞，线程资源浪费严重。Reactor vs BIO：Reactor 用少量线程处理大量连接（事件驱动），BIO 线程数 = 连接数。NIO（New I/O）是 Reactor 的底层基础。",
          "difficulty": 3
        },
        {
          "id": "nw-006",
          "question": "进程和线程的区别？为什么需要多线程而不是多进程？",
          "answer": "进程：资源分配的基本单位，有独立的地址空间、文件描述符、堆栈。线程：CPU 调度的基本单位，同一进程内的线程共享进程的资源（内存、文件描述符），每个线程有独立的栈和寄存器。区别：创建/销毁开销（进程远大于线程）、切换成本（进程需切换地址空间，线程只需保存/恢复寄存器）、通信方式（进程需 IPC，线程可直接共享内存）。Java 为什么用多线程：(1) Java 本身就是多线程的（GC 线程、Main 线程等）；(2) 线程创建和切换成本低；(3) 共享内存通信简单。注意：线程安全问题（可见性、原子性、有序性）需要同步机制保障。",
          "difficulty": 1
        },
        {
          "id": "nw-007",
          "question": "epoll 和 select/poll 的区别？为什么 Netty 用 epoll？",
          "answer": "select：文件描述符数量有限制（FD_SETSIZE 默认 1024），每次调用需传入全部 fd 集合，内核需要遍历全部 fd 检查就绪事件，O(n) 复杂度。poll：与 select 类似，但用链表存储 fd，无数量限制，仍需遍历。epoll（Linux）：(1) 无数量限制；(2) 内核维护就绪事件列表，不需要每次传入全部 fd（epoll_wait 只返回就绪的 fd），O(1) 复杂度（活跃 fd 数）；(3) 支持水平触发（LT）和边缘触发（ET，只通知一次，更高效但编程复杂）。Netty 用 epoll：Linux 上默认使用 epoll，利用 ET 模式减少系统调用次数，适合高并发连接场景。",
          "difficulty": 3
        },
        {
          "id": "nw-008",
          "question": "HTTPS 和 HTTP 的区别？对称加密和非对称加密各自的角色？",
          "answer": "HTTPS = HTTP + TLS/SSL。区别：HTTPS 加密传输、可验证服务器身份（证书）、数据完整性（防篡改）。HTTP 明文传输，易被窃听和篡改。对称加密：加密和解密用同一密钥，速度快（AES、DES），适合大量数据加密。问题：密钥如何安全传输给对方？非对称加密：公钥加密私钥解密（RSA、ECC），速度慢（比对称加密慢 100~1000 倍），适合小数据（密钥交换）。HTTPS 握手中：用非对称加密传输预主密钥（解决密钥分发问题），后续用预主密钥派生的会话密钥对称加密通信（兼顾安全和性能）。",
          "difficulty": 2
        }
      ]
    },
    {
      "id": "system-design",
      "label": "系统设计场景",
      "icon": "🏗️",
      "description": "秒杀、短链、Feed流、延时任务等场景",
      "source": "system-design-scenarios.md",
      "questions": [
        {
          "id": "sd-001",
          "question": "如何设计一个秒杀系统？请描述核心架构和关键手段",
          "answer": "核心挑战：瞬时洪峰（QPS 可能是平时的百倍）、避免超卖、保证最终一致性。架构设计：(1) 前端：静态化页面、按钮防抖、验证码削峰、答题验证；(2) 网关：限流（令牌桶/漏桶）、黑名单；(3) 服务层：异步下单（MQ 削峰填谷）、预扣库存（Redis DECR）、幂等键防重复下单；(4) 数据层：库存预热到 Redis、乐观锁扣减（UPDATE stock SET count=count-1 WHERE id=? AND count>0）、订单异步落库。关键流程：用户请求 → 网关限流 → 校验+Redis 预扣库存 → MQ 发送下单消息 → 消费者创建订单 → 超时未支付回补库存。风险点：热点 Key、缓存击穿、消息堆积、库存回补失败。",
          "difficulty": 3
        },
        {
          "id": "sd-002",
          "question": "如何设计一个短链系统？",
          "answer": "目标：长链接转短链接，点击短链 302 重定向到长链，支持访问统计。核心组件：(1) 短码生成：号段分配（预分配一段 ID，应用本地生成，减少数据库压力）或哈希（MD5/SHA256 取前 N 位）+ 冲突处理（加盐重试）。Base62 编码（a-z, A-Z, 0-9）缩短 URL。(2) 存储：短码→长链映射，MySQL + Redis 缓存热点短链。(3) 重定向：短链服务查询映射，302 重定向（浏览器不缓存 302，可统计点击次数）。扩展：防刷（频率限制）、防爆破（短码长度够长）、过期回收（TTL）、点击明细异步落库（MQ）。",
          "difficulty": 2
        },
        {
          "id": "sd-003",
          "question": "Feed 流的推模式和拉模式各是什么？推拉结合怎么设计？",
          "answer": "推模式（写扩散）：用户发帖时推送到所有粉丝的收件箱。优点：读时直接取收件箱，延迟低。缺点：写放大严重，大 V 粉丝多导致写入瓶颈。拉模式（读扩散）：用户刷新时拉取关注列表的最新帖子。优点：写简单，无写放大。缺点：读时聚合慢，延迟高。推拉结合（读写分离）：大 V 发帖存入发件箱（拉模式），普通用户发帖推送到粉丝收件箱（推模式）。用户刷新时：拉取关注的大 V 最新帖子 + 读取自己的收件箱 → 合并排序。关键：时间线有序、分页游标（避免重复和遗漏）、防回刷穿透（缓存 + 分页限制）。",
          "difficulty": 3
        },
        {
          "id": "sd-004",
          "question": "延时任务有哪些实现方案？各自适用什么场景？",
          "answer": "(1) 定时任务扫描（XXL-JOB/Spring Scheduled）：定时查询数据库中到期任务。实现简单但不精确（扫描间隔延迟），数据库压力大。(2) JDK DelayQueue：内存中基于优先级队列的延时队列。适合单机小量任务，重启丢失。(3) Redis 延迟队列：ZSet（score 为执行时间戳）+ 定时轮询；Redis Stream 的 XREADGROUP BLOCK。适合中小规模。(4) MQ 延迟消息：RocketMQ 原生支持 18 个延迟级别；RabbitMQ 死信队列 + TTL 实现；Kafka 需自己实现（时间轮）。适合大规模分布式场景。关键问题：任务可靠投递、重复执行防护（幂等）、取消与重试、积压监控。",
          "difficulty": 2
        },
        {
          "id": "sd-005",
          "question": "扫码登录的状态机和安全性设计？OAuth2 授权码模式的核心流程？",
          "answer": "扫码登录状态机：待扫码 → 已扫码待确认 → 确认登录 / 取消 / 过期。客户端轮询或 WebSocket 获取状态变化。安全性：扫码时生成一次性 token，防止重放攻击。OAuth2 授权码模式：用户访问客户端 → 重定向到授权服务器（带 client_id + redirect_uri + state）→ 用户登录并授权 → 授权服务器重定向回客户端（带授权码 code + state）→ 客户端用 code 换取 access_token（后端请求，不暴露给浏览器）→ 使用 access_token 访问资源。安全点：state 防 CSRF、回调域名白名单、code 一次性使用、短期 access_token + 可撤销 refresh token。",
          "difficulty": 3
        },
        {
          "id": "sd-006",
          "question": "如何设计一个支持百万 QPS 的分布式 ID 生成服务？",
          "answer": "方案选型与架构：(1) 雪花算法（Snowflake）：每个机器独立生成，无网络依赖。通过 ZooKeeper/Nacos 分配 workerId。单机 QPS 约 400 万。注意时钟回拨处理。(2) 号段模式（Leaf-segment）：数据库预分配号段（如一次分配 1000 个），应用在内存中使用，用完再申请。双 Buffer 预分配避免临界点等待。数据库只需 UPDATE 一行，支持高并发。(3) 号段 + Snowflake 混合：号段分配 workerId，Snowflake 生成 ID。百万 QPS 架构：多节点水平部署、负载均衡、监控各节点余量、自动补充号段。可靠性：数据库高可用、降级策略（号段用完前提前申请）、监控告警。",
          "difficulty": 3
        },
        {
          "id": "sd-007",
          "question": "如何设计一个站内消息系统？",
          "answer": "消息类型拆分：系统通知（全量推送）、事件提醒（单点推送，如点赞/评论）、私信（点对点）。核心能力：(1) 未读计数：Redis 计数器（INCR/DECR），读消息时清零；(2) 聚合展示：同一类型消息合并（如「X等3人赞了你的文章」）；(3) 已读状态：按会话维度维护已读水位（cursor），而非逐条标记；(4) 离线补偿：用户上线时推送离线期间的消息；(5) 幂等去重：消息 ID 唯一约束。存储策略：会话列表（最近 N 个会话）和消息明细分离；消息明细冷热分层（热消息在 Redis，冷消息在 MySQL/MongoDB）；按用户 ID 分库分表。推送通道：WebSocket（实时在线推送）+ MQ（异步离线推送）。",
          "difficulty": 3
        },
        {
          "id": "sd-008",
          "question": "如何设计 UV/PV 统计系统？",
          "answer": "PV（Page View）：页面浏览次数，简单计数器即可。复杂场景按站点/页面/时间维度分桶。UV（Unique Visitor）：独立访客数，需去重。常见实现：(1) HyperLogLog（Redis PFADD/PFCOUNT）：基于概率算法，标准误差 0.81%，12KB 固定内存统计 2^64 个元素。适合大规模去重统计。(2) Bitmap：适合中小规模（用户 ID 连续且有上限），每个 bit 表示一个用户是否访问。精确但内存随用户数增长。(3) Set：精确但内存大（每个元素约 64 字节），不推荐大规模场景。大盘链路：埋点 SDK 采集 → 消息队列削峰 → 实时消费写入 Redis HyperLogLog → 离线/准实时聚合到分析系统。多维分析可借助 ClickHouse/Elasticsearch。",
          "difficulty": 2
        },
        {
          "id": "sd-009",
          "question": "登录风控（密码错误限制）如何设计？",
          "answer": "核心：用户维度失败计数 + TTL 锁定窗口。设计要点：(1) 分布式一致计数：Redis INCR + EX（设置过期时间），每次登录失败计数+1，成功则清除计数；(2) 风控阈值分级：失败 3 次 → 验证码；失败 5 次 → 账号临时锁定 15 分钟；失败 10 次 → 需人工校验/联系客服；(3) 防账号枚举：验证码在 N 次失败后出现（而非首次），避免通过验证码差异判断账号是否存在；(4) 审计：记录关键操作日志（IP、设备指纹、时间），异常时告警；(5) 限流：同一 IP 高频登录尝试直接拦截。注意：Redis key 设计（如 login:fail:{userId}），过期时间随失败次数递增。",
          "difficulty": 2
        },
        {
          "id": "sd-010",
          "question": "面试答题通用框架是什么？如何回答系统设计题？",
          "answer": "通用框架四步：(1) 澄清约束：QPS、峰值倍数、SLA、数据量级、延迟目标、一致性级别、成本上限。如「1000 QPS，峰值 10 倍，99.9% 可用，数据量 TB 级」。(2) 给出核心链路：请求路径（客户端→网关→服务→数据库）、读写路径（缓存→数据库→异步更新）、关键状态流转、失败路径。(3) 工程细节：容量估算（存储量、带宽、连接数）、监控告警、降级兜底（缓存降级、限流）、回滚与补偿。(4) 权衡取舍：每个设计决策说明 why（为什么选 A 不选 B，trade-off 是什么）。面试官关注：思考框架清晰度、关键细节深度、对瓶颈的敏感度、权衡取舍的能力。",
          "difficulty": 1
        }
      ]
    },
    {
      "id": "frontend",
      "label": "前端框架",
      "icon": "⚛️",
      "description": "React/Vue 核心机制、状态管理、工程化",
      "source": "react-vue.md",
      "questions": [
        {
          "id": "fe-001",
          "question": "React Fiber 架构解决了什么问题？Diff 算法的核心策略？",
          "answer": "Fiber 架构：React 16 引入，将组件渲染树从递归树结构改为链表结构，支持可中断渲染。解决了 React 15 的递归渲染无法中断的问题（长时间阻塞主线程导致卡顿）。核心能力：时间切片（Time Slicing）和优先级调度（高优先级任务如交互可中断低优先级任务如渲染）。Diff 算法策略：(1) 同层比较（只比较同一层级的节点，不跨层级）；(2) key 的作用（帮助识别节点是否可复用，无 key 时按顺序匹配，可能导致不必要的更新）。O(n) 复杂度：通过三个假设（跨层级不移动、不同类型不同子树、key 标识节点身份）将 O(n³) 降到 O(n)。",
          "difficulty": 3
        },
        {
          "id": "fe-002",
          "question": "Vue 3 的 Proxy 响应式和 Vue 2 的 defineProperty 有什么本质区别？",
          "answer": "Vue 2（defineProperty）：对对象的每个属性逐一劫持 getter/setter，需要递归遍历对象（性能差），无法检测属性的新增/删除（需 $set/$delete），无法监听数组索引修改（需重写数组方法）。Vue 3（Proxy）：代理整个对象，不需要递归遍历（懒代理，访问到嵌套属性时才代理），支持动态属性新增/删除的检测，支持 Map/Set/WeakMap/WeakSet。性能优势：初始化时不用遍历所有属性（对象大时差异明显），内存占用更少。响应式效果更完整，API 更简洁（ref/reactive/computed/watch）。",
          "difficulty": 3
        },
        {
          "id": "fe-003",
          "question": "React 的 useEffect 和 useLayoutEffect 有什么区别？",
          "answer": "useEffect：异步执行，在浏览器绘制（paint）之后执行。适合不阻塞 UI 更新的副作用（数据请求、事件绑定、日志）。useLayoutEffect：同步执行，在浏览器绘制之前、DOM 更新之后执行。适合需要读取/修改 DOM 布局的场景（如测量元素尺寸、滚动位置同步、防止闪烁）。如果 useEffect 导致视觉闪烁（如先显示旧状态再切换到新状态），改用 useLayoutEffect。SSR 场景：useLayoutEffect 会在服务端警告，因为服务端没有 DOM 布局的概念，可用 useEffect 替代。",
          "difficulty": 2
        },
        {
          "id": "fe-004",
          "question": "大列表渲染卡顿怎么优化？虚拟列表的原理？",
          "answer": "优化方案：(1) 虚拟列表（Virtual List / Virtual Scrolling）：只渲染可视区域内的元素 + 上下缓冲区，大量数据时 DOM 节点数恒定。原理：计算可视区域的起始和结束索引，动态设置容器的 transform/padding 模拟完整高度。React: react-window/react-virtualized；Vue: vue-virtual-scroller。(2) 分页/懒加载：滚动到底部加载更多。(3) 时间分片（React 18 useTransition）：将渲染任务拆分为小块，避免长时间阻塞。(4) 减少不必要的渲染：React.memo、useMemo、useCallback；Vue v-memo。(5) 固定高度优先，变高需要测量或预估。",
          "difficulty": 2
        },
        {
          "id": "fe-005",
          "question": "SSR、CSR、SSG 的区别？Next.js 和 Nuxt.js 的混合渲染模式？",
          "answer": "CSR（Client-Side Rendering）：浏览器下载空 HTML + JS，JS 执行后渲染页面。优点：交互体验好、服务器压力小。缺点：首屏慢、SEO 差。SSR（Server-Side Rendering）：服务器渲染 HTML 返回，客户端 hydration 绑定事件。优点：首屏快、SEO 好。缺点：服务器压力大、TTFB 增加。SSG（Static Site Generation）：构建时生成静态 HTML。优点：极致性能、CDN 友好。缺点：不适合动态内容。Next.js/Nuxt.js 混合渲染：同一应用中不同页面可选不同渲染模式：SSR（动态页面）、SSG（静态页面）、ISR（增量静态再生，构建后按需更新）、CSR（纯客户端）。按路由级别灵活配置。",
          "difficulty": 2
        },
        {
          "id": "fe-006",
          "question": "React Hooks 的闭包陷阱是什么？怎么解决？",
          "answer": "闭包陷阱：useEffect 中引用的变量在创建时就固定了（闭包捕获），后续渲染时如果变量值变化，回调中看到的仍是旧值（stale closure）。典型场景：setInterval/setTimeout 中读取 state 总是旧值。解决方案：(1) 使用 ref 保存最新值（useRef + .current 始终指向最新值）；(2) 正确设置依赖数组（将所有使用的变量加入 deps，但这会导致 effect 频繁重新执行）；(3) 使用函数式更新 setState(prevState => newState)，不依赖外部变量获取最新状态。最佳实践：依赖数组中不要遗漏变量，ESLint 的 react-hooks/exhaustive-deps 规则可帮助检测。",
          "difficulty": 2
        },
        {
          "id": "fe-007",
          "question": "Vite 和 Webpack 的区别？为什么 Vite 开发更快？",
          "answer": "Vite：基于 ESM（ES Modules），开发服务器利用浏览器原生 ESM 加载，按需编译（只编译当前请求的模块），无需打包。HMR（热更新）速度与项目规模无关。生产构建使用 Rollup。Webpack：打包所有模块为 bundle，项目越大启动越慢（全量打包），HMR 速度随项目规模下降。Vite 开发更快的原因：(1) 无需打包（利用浏览器原生 ESM）；(2) 按需编译（只编译请求的文件）；(3) 预构建（esbuild 预构建依赖，比 Webpack 快 10~100 倍）。注意：生产环境 Vite 用 Rollup 打包，和开发模式不同。",
          "difficulty": 2
        },
        {
          "id": "fe-008",
          "question": "React 和 Vue 的状态管理方案对比？各自适合什么场景？",
          "answer": "React 状态管理：Context API（内置，轻量，跨层级传递，但频繁更新会导致重渲染）→ Redux（单向数据流，可预测，适合大型项目，但样板代码多）→ Zustand（极简 Hook 式，无 Provider，推荐中小项目）→ Jotai/Recoil（原子化状态，细粒度更新）。Vue 状态管理：Pinia（Vue 3 官方推荐，TS 友好，无 mutations，支持模块化，DevTools 集成）→ Vuex（Vue 2 时代，mutation/action 分离，Vue 3 不推荐）。选择建议：React 新项目用 Zustand 或 Jotai；Vue 新项目用 Pinia；大型复杂项目 React 用 Redux Toolkit，Vue 用 Pinia 即可。",
          "difficulty": 2
        },
        {
          "id": "fe-009",
          "question": "React 18 的并发特性有哪些？useTransition 和 useDeferredValue 怎么用？",
          "answer": "React 18 并发特性：并发渲染（Concurrent Rendering），React 可以中断渲染处理更高优先级的更新。useTransition：将状态更新标记为非紧急，返回 [isPending, startTransition]。startTransition 内的更新不阻塞用户输入。适合：搜索框输入（输入实时响应，搜索结果延迟渲染）、大数据列表切换。useDeferredValue：返回一个延迟版本的值，当前有紧急更新时使用旧值渲染。用法：const deferredQuery = useDeferredValue(query)。适合：过滤/搜索场景。Suspense：React.lazy + Suspense 实现代码分割和异步数据获取的统一 loading UI。注意：并发特性只在 Concurrent Mode（createRoot）下生效。",
          "difficulty": 3
        },
        {
          "id": "fe-010",
          "question": "前端组件通信有哪些方式？",
          "answer": "React：(1) props（父→子）；(2) 回调函数（子→父）；(3) Context API（跨层级，适合主题/用户信息等全局数据）；(4) 状态管理（Redux/Zustand，任意组件间）；(5) 事件总线（自定义 EventEmitter，不推荐，React 单向数据流）；(6) Ref 转发（forwardRef + useImperativeHandle）。Vue：(1) props / emit（父子）；(2) provide/inject（跨层级）；(3) Pinia/Vuex（全局状态）；(4) 事件总线（mitt 库）；(5) $refs（父访问子组件实例）；(6) $attrs（透传属性）。跨 iframe/跨域：postMessage。",
          "difficulty": 1
        }
      ]
    }
  ]
};

if (typeof module !== 'undefined') {
  module.exports = QUIZ_DATA;
}
