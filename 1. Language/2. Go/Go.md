1. Go是一种编译型语言，类C语法，并且有垃圾回收

# 1. Go的数据类型

## 1.1 类型介绍

- 基本类型，可以通过unsafe.Sizeof()计算变量内存空间
- Go语言中无论是全局变量还是局部变量,只要定义了一个变量都有默认的0值

| 类型               | 32位编译器 | 64位编译器 | 本质                                        |
| ------------------ | ---------- | ---------- | ------------------------------------------- |
| bool               |            |            |                                             |
| string             |            |            |                                             |
|                    |            |            |                                             |
| int8/uint8(byte)   | 1          | 1          | signed char/unsigned char                   |
| int16/uint16       | 2          | 2          | signed short/unsigned short                 |
| int32(rune)/uint32 | 4          | 4          | signed int/unsigned int                     |
| int64/uint64       | 8          | 8          | signed long long int/unsigned long long int |
| int / uint         | 4          | 8          | 根据机器位数决定长度                        |
| uintptr            | 4          | 8          | 根据机器位数决定长度 uint32/uint64          |
|                    |            |            |                                             |
| float32            | 4          | 4          | float                                       |
| float64            | 8          | 8          | double                                      |
|                    |            |            |                                             |

- 复合类型：struct，array，slice，map，channel
  - 使用nil作为默认值

- interface

## 1.2 变量的声明初始化

1. 单个声明

```go
var <变量名称> <数据类型> = <值>;
var <变量名称> = <值>;
// 只能用于局部变量
<变量名称> := <值>;
```

2. 变量组

```go
var( // 一行定义多个
    num7, num8 = 70, 80
    num9, num10 = 9.99, 100
)
```

## 1.3 常量的声明初始化

常量只能是bool， 数字类型，浮点类型，复数，字符串类型

数字型的常量是没有大小和符号的，并且可以使用任何精度而不会导致溢出：

1. 普通const

```go
const length int = 10
```

2. const定义枚举类型

```go
const(
    //每行的iota都会累加1，第一行的iota默认为0
    // 这里面每行都是 10 * iota
    BEIJING = 10 * iota //iota = 0
    SHANGHAI
)
```

![image-20211225001114800](http://aikaid-img.oss-cn-shanghai.aliyuncs.com/img/image-20211225001114800.png)

## 1.4 类型转换

- Go语言中数据`只能显示转换`， 格式: `数据类型(需要转换的数据)`

- 数值类型转字符串类型`strconv..FormatXxx()`

```go
// 第一个参数: 需要被转换的整型,必须是int64类型
// 第二个参数: 转换为几进制,  必须在2到36之间
str1 := strconv.FormatInt(int64(num1), 10)

// float64转换为字符串
// 第二个参数: 转换为什么格式,f小数格式, e指数格式
// 第三个参数: 转换之后保留多少位小数, 传入-1按照指定类型有效位保留
// 第四个参数: 被转换数据的实际位数,float32就传32, float64就传64
str3 := strconv.FormatFloat(num5, 'f', -1, 64)
// bool转化为字符串
var num6 bool = true
str7 := strconv.FormatBool(num6)
fmt.Println(str7) // true
```

- 字符串类型转数值类型`strconv.ParseXxx()`

```go
// 第一个参数: 需要转换的数据
// 第二个参数: 转换为几进制
// 第三个参数: 转换为多少位整型
// 注意点: ParseInt函数会返回两个值, 一个是转换后的结果, 一个是错误
num1, err := strconv.ParseInt(str1, 10, 8)

// 第一个参数: 需要转换的数据
// 第二个参数: 转换为多少位小数, 32 or 64
// ParseFloat同样有两个返回值, 如果能够正常转换则错误为nil, 否则不为nil
num3, err := strconv.ParseFloat(str3, 32)
// 字符串转化为bool
num4, _ := strconv.ParseBool(str4)
```

- 快速转换

```go
// 快速将整型转换为字符串类型
// 注意:Itoa方法只能接受int类型
var str1 string = strconv.Itoa(int(num1))

// 快速将字符串类型转换为整型
// 注意: Atoi方法返回两个值, 一个值是int,一个值是error
// 如果字符串能被转换为int,那么error为nil, 否则不为nil
num2, err := strconv.Atoi(str2)
```

## 1.5 输入输出

- Printf 可以使用%b输出二进制，%T输出值的类型，%v打印所有类型数据
- Sprintx 将字符串返回给我们

## 1.6 生命周期

- 局部变量有动态的生命周期：变量一直生存到它变得不可访问，所以局部变量可能在其所对应的代码块之外存活
- 局部变量根据逃逸分析来决定分配到栈还是堆，而不是由var或者new关键字来决定

## 1.7 类型声明/ 别名

- 类型声明(类型定义)，定义以后本质上就是两种不同的类型，就不能进行算数运算等操作

```go
type name underlying-type
```

- 类型别名

```go
type name = underlying-type
```



# 2. 函数

## 2.1 普通函数

基本结构

```go
func  (接收者 接受者类型)函数名称(形参列表)(返回值列表) {
        函数体相关语句;
        return 返回值;
}
```

其中：

- parameter_list 的形式为 (param1 type1, param2 type2, …)
- return_value_list 的形式为 (ret1 type1, ret2 type2, …)

多返回值

```go
func foo2(a string, b int) (int, int){
    return 666,777
}

func foo3(a string, b int) (r1 int,r2 int){
    r1 = 1000
    r2 = 2000
    return 
}

func foo4(a string, b int) (r1,r2 int){
    
}
```

## 2.2 匿名函数

```go
a := func(s string) {
    fmt.Println(s)
}
a("hello lnj")
```

## 2.3 闭包

- 闭包是一个特殊的匿名函数, 它是匿名函数和相关引用环境组成的一个整体
- 也就是说只要匿名函数中用到了外界的变量, 那么这个匿名函数就是一个闭包
- 只要闭包还在使用外界的变量, 那么外界的变量就会一直存在

```go
package main

import "fmt"

func adder() func(int) int {
	sum := 0
	return func(x int) int {
		sum += x
		return sum
	}
}

func main() {
	pos, neg := adder(), adder()
	for i := 0; i < 10; i++ {
		fmt.Println(
			pos(i),
			neg(-2*i),
		)
	}
}

```

## 2.4 defer

- Go语言提供了defer语句用于实现其它面向对象语言析构函数的功能

- defer语句常用于`释放资源`、`解除锁定`以及`错误处理`等

- defer调用的参数会立即计算，但是函数执行会在所属函数执行完毕之后才会执行, 并且如果注册了多个defer语句,那么它们会按照`后进先出`的原则执行

  

## 2.5 init函数

- init函数用于处理当前文件的初始化操作, 在使用某个文件时的一些准备工作应该放到这里

- 单个包中代码执行顺序 ： ***`main包-->常量-->全局变量-->init函数-->main函数-->Exit`***

- 多个包之间代码执行顺序如下

![](http://aikaid-img.oss-cn-shanghai.aliyuncs.com/img/b17558afeb98b97b90f2c45e2faea7aa.png)

## 2.6 方法

- Go语言中的方法`一般用于`将`函数和结构体绑定在一起`, 让结构体除了能够保存数据外还能具备某些行为
- 将函数和数据类型绑定的格式： 只需要在函数名称前面加上(接收者 数据类型), 即可将函数和某种数据类型绑定在一起
- 方法接收者在它自己的参数列表内，位于 `func` 关键字和方法名之间

```go
func (接收者 数据类型)方法名称(形参列表)(返回值列表){
  方法体
}
// 示例
// 1.定义一个结构体
type Person struct {
	name string
	age int
}
// 2.定义一个函数, 并将这个函数和Person结构体绑定在一起
func (p Person)say()  {
	fmt.Println("my name is", p.name, "my age is", p.age)
}
func main() {
	// 3.创建一个结构体变量
	per := Person{"lnj", 33}
	// 4.利用结构体变量调用和结构体绑定的方法
	// 调用时会自动将调用者(per)传递给方法的接收者(p)
	// 所以可以在方法内部通过p方法结构体变量的属性
	per.say()
}
```



# 3. 流程控制

## 3.1 if

在 `if` 的初始化语句中声明的变量同样可以在任何对应的 `else` 块中使用

```go
if 初始化语句; 条件表达式{
    语句块;
}
if age := 18; age >= 18{
    fmt.Println("成年人")
}
```

## 3.2 switch

- 一个case后面可以有多个表达式, 满足其中一个就算匹配
- case后面不需要添加break
- case后面不仅仅可以放常量,还可以放变量, 函数，表达式

```go
switch 初始化语句; 表达式{
  case 表达式1, 表达式2:
        语句块;
  case 表达式1, 表达式2:
        语句块;
  default:
        语句块;
}
```

```go
switch num := 3;num {
    case 1,2,3,4,5:
    fmt.Println("工作日")
    case 6,7:
    fmt.Println("非工作日")
    default:
    fmt.Println("Other...")
}
```

## 3.3 for



```go
for 初始化表达式；循环条件表达式；循环后的操作表达式 {
    循环体语句;
}
for i:=0; i<10; i++{
    fmt.Println(i)
}
// 简化为while
for i < 10 {
    fmt.Println(i)
}
```

for...rangr

```go
for 索引, 值 := range 被遍历数据{
}
// 1.定义一个数组
arr := [3]int{1, 3, 5}
// 2.快速遍历数组
// i用于保存当前遍历到数组的索引
// v用于保存当前遍历到数组的值
for i, v := range arr{
    fmt.Println(i, v)
}
// 其他形式
for in, _ := range pow
for _, value := range pow
```

## 3.4 跳转

- 利用break可以跳转到指定标签，跳转到标签之后switch和循环不会再次被执行
- continue语句可以指定标签，标签后面只能跟循环语句, 不能插入其它语句

# 4. 数组

## 4.1 普通数组

- 格式:`var arr [元素个数]数据类型`
- Go语言中数组定义之后就`有默认的初始值`
- Go语言中`数组长度`也是`数据类型的一部分`
- 如果元素类型支持==、!=操作时,那么数组也支持此操作
- Go语言中的数组是值类型, 赋值和传参时会复制整个数组

```go
// 1.定义的同时完全初始化
var arr1 [3]int = [3]int{1, 3, 5}
// 2.打印数组
fmt.Println(arr1) // [1 3 5]

// 1.定义的同时部分初始化
var arr4 [3]int = [3]int{8, 9}
// 2.打印数组
fmt.Println(arr4) // [8 9 0]

// 1.定义的同时指定元素初始化
var arr5 [3]int = [3]int{0:8, 2:9}
// 2.打印数组
fmt.Println(arr5) // [8 0 9]

// 1.先定义再逐个初始化
var arr3 [3]int
arr3[0] = 1
arr3[1] = 2
arr3[2] = 3
// 2.打印数组
fmt.Println(arr3) // [1 2 3]
```

## 4.2 slice

1. Go语言中的切片本质上是一个结构体

```go
type slice struct{
  array unsafe.Pointer // 指向底层数组指针
  len int // 切片长度(保存了多少个元素)
  cap int // 切片容量(可以保存多少个元素)
}
```

2. 创建切片的三种方式

- 方式一: 通过数组创建切片`array[startIndex:endIndex]`

```go
var arr = [5]int{1, 3, 5, 7, 9}
// 同时指定开始位置和结束位置
var sce1 = arr[0:2]
fmt.Println(sce1) // [1 3]
```

- 方式二: 通过make函数创建` make(类型, 长度, 容量)`

```go
// 参数为： 数据类型， len， cap
var sce = make([]int, 3, 5)
/*
	内部实现原理
	var arr = [5]int{0, 0, 0}
	var sce = arr[0:3]
*/
```

- 方式三:通过Go提供的语法糖快速创建

```go
// 和创建数组一模一样, 但是 不能指定长度
// 通过该方式创建时, 切片的长度和容量相等
var sce = []int{1, 3, 5}
```

3. 切片的使用

- 增：append函数会在切片`末尾`添加一个元素, 并返回一个追加数据之后的切片，如果追加之后没有超出切片的容量,那么返回原来的切片, 如果追加之后超出了切片的容量,那么返回一个新的切片；每次扩容是原来的两倍

```go
// 
func append(s []T, vs ...T) []T
```

- 改：通过`切片名称[索引]`方式操作切片
- 复制: 格式: `copy(目标切片, 源切片)`, 会将源切片中数据拷贝到目标切片中
- 切片只支持判断是否为nil, 不支持==、!=判断
- 字符串的底层是[]byte数组, 所以字符也支持切片相关操作

> A nil slice has a length and capacity of 0 and has no underlying array.

## 4.3 map

key的选择

- 只要是可以做==、!=判断的数据类型都可以作为key(数值类型、字符串、数组、指针、结构体、接口)。 
- map的key的数据类型不能是:slice、map、function。

格式

- map格式:`var dic map[key数据类型]value数据类型`

```go
package main
import "fmt"
func main() {
    var dic map[int]int = map[int]int{0:1, 1:3, 2:5}
    fmt.Println(dic) // map[0:1 1:3 2:5]

    // 获取map中某个key对应的值
    fmt.Println(dic[0]) // 1

    // 修改map中某个key对应的值
    dic[1] = 666
    fmt.Println(dic) // map[0:1 1:666 2:5]
}
```

创建map

- 方式一: 通过Go提供的语法糖快速创建

```go
dict  := map[string]string{"name":"lnj", "age":"33", "gender":"male"}
```

- 方式二:通过make函数创建`make(类型, [容量])`

```go
var dict = make(map[string]string, 3)
```

map使用

- 增和改： map[key] = value
- 删除: delete(<dict>, <key>)
- 查询是否存在:

```go
if value, ok := dict["age"]; ok{
    fmt.Println("有age这个key,值为", value)
}
```

# 5. 可见性

- 以大写开头的标识符可以被外部包使用
- 以小写开头，则对包外是不可见的

# 6. 值传递和引用传递

Go语言中`值类型`有: int系列、float系列、bool、string、数组、结构体

+ 值类型通常在栈中分配存储空间
+ 值类型作为函数参数传递, 是拷贝传递
+ 在函数体内修改值类型参数, 不会影响到函数外的值

Go语言中`引用类型`有: 指针、slice、map、channel

+ 引用类型通常在堆中分配存储空间
+ 引用类型作为函数参数传递,是引用传递
+ 在函数体内修改引用类型参数,会影响到函数外的值

# 7. 结构体

定义结构体

```go
type 类型名称 struct{
  属性名称 属性类型
  属性名称 属性类型
  ... ...
}
```

创建结构体变量

- 先定义结构体类型, 再定义结构体变量

```go
package main
import "fmt"
func main() {
    type Student struct {
        name string
        age int
    }	

    // 完全初始化
    var stu1= Student{"lnj", 33}
    fmt.Println(stu1)
    // 部分初始化
    // 部分初始化必须通过 属性名称: 方式指定要初始化的属性
    var stu2 = Student{name:"lnj"}
    fmt.Println(stu2)
}
```

- 定义结构体类型同时定义结构体变量(匿名结构体). 用于只使用一次的结构体

```go
package main
import "fmt"
func main() {
    // 注意: 这里不用写type和结构体类型名称
    var stu2 = struct {
        name string
        age int
    }{
        name: "lnj",
        age: 33,
    }
    fmt.Println(stu2)
}
```

- 只有属性名、属性类型、属性个数、排列顺序都相同的结构体类型才能转换
- 虽然类型名称不一样, 但是两个类型中的`属性名称`、`属性类型`、`属性个数`、`排列顺序`都一样,所以可以强制转换

# 8. 指针

1. 存放的内容：其他变量的地址
2. 占据的内存：32位4个字节, 64位8个字节
3. 对于数组：&arr的类型是*[3]int, &arr[0]的类型是*int；
4. 对于切片：

- 切片的本质就是一个指针指向数组, 所以指向切片的指针是一个二级指针

5. 对于结构体

- 创建结构体指针

```go
package main
import "fmt"
type Student struct {
    name string
    age int
}
func main() {
    // 1. 创建时利用取地址符号获取结构体变量地址
    var p1 = &Student{"lnj", 33}
    fmt.Println(p1) // &{lnj 33}

    // 2. 通过new内置函数传入数据类型创建
    // 内部会创建一个空的结构体变量, 然后返回这个结构体变量的地址
    var p2 = new(Student)
    fmt.Println(p2) // &{ 0}
}
```

- 操作结构体

```go
package main
import "fmt"
type Student struct {
    name string
    age int
}
func main() {
    var p = &Student{}
    // 方式一: 传统方式操作
    // 修改结构体中某个属性对应的值
    // 注意: 由于.运算符优先级比*高, 所以一定要加上()
    (*p).name = "lnj"
    // 获取结构体中某个属性对应的值
    fmt.Println((*p).name) // lnj

    // 方式二: 通过Go语法糖操作
    // Go语言作者为了程序员使用起来更加方便, 在操作指向结构体的指针时可以像操作接头体变量一样通过.来操作
    // 编译时底层会自动转发为(*p).age方式
    p.age = 33
    fmt.Println(p.age) // 33
}
```

# 9. 接口

- 定义接口, **接口类型** 是由一组方法签名定义的集合。

```go
type 接口名称 interface{
  函数声明
}
```

- 只有实现了接口中所有的方法, 才算实现了接口, 才能用`该接口类型`接收

```go
// 1.定义一个接口
type usber interface {
    start()
    stop()
}
type Computer struct {
    name string
    model string
}
// 2.实现接口中的所有方法
func (cm Computer)start() {
    fmt.Println("启动电脑")
}
func (cm Computer)stop() {
    fmt.Println("关闭电脑")
}
```

- 和结构体一样,接口中也可以嵌入接口

- 空接口类型可以接收任意类型数据

- 接口类型还原会原始类型, 或者抽线接口转化为具体接口使用 ok-idiom模式

```go
var s studier
s = Person{"lnj", 33}
// 1.利用ok-idiom模式将接口类型还原为原始类型
// s.(Person)这种格式我们称之为: 类型断言
if p, ok := s.(Person); ok {
    p.name = "zs"
    fmt.Println(p)
}
```

# 10. 继承

1. 实现方式：使用组合实现其继承
2. 继承属性：
3. 继承方法：

```go
type Person struct {
	name string
	age int
}
// 父类方法
func (p Person)say()  {
	fmt.Println("name is ", p.name, "age is ", p.age)
}

type Student struct {
	Person
	score float32
} 

func main() {
	stu := Student{Person{"zs", 18}, 59.9}
	stu.say()
}
```

# 11. 异常

## 11.1 error

1. golang中提供了两种处理异常的方式

+ 一种是程序发生异常时, 将异常信息反馈给使用者
+ 一种是程序发生异常时, 立刻退出终止程序继续运行

2. 打印异常信息

-  通过fmt包中的Errorf函数创建错误信息, 然后打印

```go
// 1.创建错误信息
var err error = fmt.Errorf("这里是错误信息")
// 2.打印错误信息
fmt.Println(err) // 这里是错误信息
```

- 通过errors包中的New函数创建错误信息,然后打印

```go
// 1.创建错误信息
var err error = errors.New("这里是错误信息")
// 2.打印错误信息
fmt.Println(err) // 这里是错误信息
```

3. 创建原理

- Go语言中创建异常信息其实都是通过一个error接口实现的
- Go语言在`builtin`包中定义了一个名称叫做error的接口. 源码如下

```go
package builtin
// 定义了一个名称叫做error的接口
// 接口中声明了一个叫做Error() 的方法
type error interface {
	Error() string
}
```

- 在errors包中定义了一个名称叫做做errorString的结构体, 利用这个结构体实现了error接口中指定的方法
- 并且在errors 包中还提供了一个New方法, 用于创建实现了error接口的结构体对象, 并且在创建时就会把指定的字符串传递给这个结构体

```go
// 指定包名为errors
package errors 
// 定义了一个名称叫做errorString的结构体, 里面有一个字符串类型属性s
type errorString struct {
	s string
}
// 实现了error接口中的Error方法
// 内部直接将结构体中保存的字符串返回
func (e *errorString) Error() string {
	return e.s
}
// 定义了一个New函数, 用于创建异常信息
// 注意: New函数的返回值是一个接口类型
func New(text string) error {
        // 返回一个创建好的errorString结构体地址
	return &errorString{text}
}
```

fmt包中Errorf底层的实现原理其实就是在内部自动调用了errors包中的New函数

```go
func Errorf(format string, a ...interface{}) error {
	return errors.New(Sprintf(format, a...))
}
```

应用场景

```go
package main
import "fmt"
func div(a, b int) (res int, err error) {
	if(b == 0){
		// 一旦传入的除数为0, 就会返回error信息
		err = errors.New("除数不能为0")
	}else{
		res = a / b
	}
	return
}
func main() {
	//res, err := div(10, 5)
	res, err := div(10, 0)
	if(err != nil){
		fmt.Println(err) // 除数不能为0
	}else{
		fmt.Println(res) // 2
	}
}
```

## 11.2 中断

- Go语言中提供了一个叫做panic函数, 用于发生异常时终止程序继续运行

```go
package main
import "fmt"
func div(a, b int) (res int) {
	if(b == 0){
		//一旦传入的除数为0, 程序就会终止
		panic("除数不能为0")
	}else{
		res = a / b
	}
	return
}
func main() {
	res := div(10, 0)
	fmt.Println(res)
}
```

## 11.3 恢复

在Go语言中我们可以通过defer和recover来实现panic异常的捕获, 让程序继续执行

- 多个异常,只有第一个会被捕获
- 如果有异常写在defer中, 那么只有defer中的异常会被捕获

```go
package main
import "fmt"
func div(a, b int) (res int) {
	// 定义一个延迟调用的函数, 用于捕获panic异常
	// 注意: 一定要在panic之前定义
	defer func() {
		if err := recover(); err != nil{
			res = -1
			fmt.Println(err) // 除数不能为0
		}
	}()
	if(b == 0){
		//err = errors.New("除数不能为0")
		panic("除数不能为0")
	}else{
		res = a / b
	}
	return
}

func setValue(arr []int, index int ,value int)  {
	arr[index] = value
}
func main() {
	res := div(10, 0)
	fmt.Println(res) // -1
}
```

# 12. Go并发

- Go在语言级别支持`协程`(多数语言在语法层面并不直接支持协程), 叫做goroutine. 

```go
package main

import (
	"fmt"
	"time"
)

func sing()  {
	for i:=0; i< 10; i++{
		fmt.Println("我在唱歌")
		time.Sleep(time.Millisecond)
	}
}
func dance() {
	for i:=0; i< 10; i++{
		fmt.Println("我在跳舞---")
		time.Sleep(time.Millisecond)
	}
}

func main() {
	// 串行: 必须先唱完歌才能跳舞
	//sing()
	//dance()

	// 并行: 可以边唱歌, 边跳舞
	// 注意点: 主线程不能死, 否则程序就退出了
	go sing() // 开启一个协程
	go dance() // 开启一个协程
	for{
		;
	}
}
```

- runtime包中常用的函数
  - Gosched:使当前go程放弃处理器，以让其它go程运行
  - Goexit: 终止调用它的go程, 其它go程不会受影响
  - NumCPU: 返回本地机器的逻辑CPU个数
  - GOMAXPROCS: 设置可同时执行的最大CPU数，并返回先前的设置

- 互斥锁
  + 互斥锁的本质是当一个goroutine访问的时候, 其它goroutine都不能访问
  + 这样就能实现资源同步, 但是在避免资源竞争的同时也降低了程序的并发性能. 程序由原来的并发执行变成了串行
- 案例: 
  + 有一个打印函数, 用于逐个打印字符串中的字符, 有两个人都开启了goroutine去打印
  + 如果没有添加互斥锁, 那么两个人都有机会输出自己的内容
  + 如果添加了互斥锁, 那么会先输出某一个的, 输出完毕之后再输出另外一个人的
```go
package main
import (
	"fmt"
	"sync"
	"time"
)
// 创建一把互斥锁
var lock sync.Mutex

func printer(str string)  {
	// 让先来的人拿到锁, 把当前函数锁住, 其它人都无法执行
	// 上厕所关门
	lock.Lock()
	for _, v := range str{
		fmt.Printf("%c", v)
		time.Sleep(time.Millisecond * 500)
	}
	// 先来的人执行完毕之后, 把锁释放掉, 让其它人可以继续使用当前函数
	// 上厕所开门
	lock.Unlock()
}
func person1()  {
	printer("hello")
}
func person2()  {
	printer("world")
}
func main() {
	go person1()
	go person2()
	for{
		;
	}
}
```

# 13. 管道



# 14. 开源书籍

## Go开源书籍推荐

- [《深入解析Go》](https://github.com/tiancaiamao/go-internals)

- [《Go实战开发》](https://github.com/astaxie/Go-in-Action)
- [《Go入门指南》](https://github.com/Unknwon/the-way-to-go_ZH_CN)
- [《Go语言标准库》](https://github.com/polaris1119/The-Golang-Standard-Library-by-Example)
- [《Go Web 编程》](https://github.com/astaxie/build-web-application-with-golang)
- [《Go语言博客实践》](https://github.com/achun/Go-Blog-In-Action)
- [《Go语言学习笔记》](https://github.com/qyuhen/book)
- [《Go语言高级编程》](https://github.com/chai2010/advanced-go-programming-book)

- [Effective Go](https://golang.org/doc/effective_go.html) 英文版
- [The Way to Go](https://github.com/Unknwon/the-way-to-go_ZH_CN) 中文版
- [《Learning Go》](https://github.com/miekg/gobook)英文版
