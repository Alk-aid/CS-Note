1. Go是一种编译型语言，类C语法，并且有垃圾回收

# 1. 变量和声明

1. 声明后初始化

```Go
package main

import (
  "fmt"
)

func main() {
  var power int
  power = 9000
  fmt.Printf("It's over %d\n", power)
}
```

2. 声明并初始化

```Go
var power int = 9000
```

3. 自动匹配

```Go
// Go 提供了一个方便的短变量声明运算符 :=
power := 9000
```

> 值得注意的是要用 `:=` 来声明变量以及给变量赋值。相同变量不能被声明两次（在相同作用域下）

4. 

```go
// go 支持多个变量同时赋值(使用 = 或者 :=）
// 另外，多个变量赋值的时候，只要其中有一个变量是新的，就可以使用:=
func main() {
  name, power := "Goku", 9000
  fmt.Printf("%s's power is over %d\n", name, power)
}
```

# 2. 常量

1. 

```go
const length int = 10
```

2. 

```go
const(
    //每行的iota都会累加1，第一行的iota默认为0
    // 这里面每行都是 10 * iota
    BEIJING = 10 * iota //iota = 0
    SHANGHAI
)
```

![image-20211225001114800](http://aikaid-img.oss-cn-shanghai.aliyuncs.com/img/image-20211225001114800.png)

# 3. 函数

基本结构

```go
//标识符 方法名(形参) 返回值
func foo1(a string,b int) int{
    
}
```

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

# 4. 导包

![image-20211225002538241](http://aikaid-img.oss-cn-shanghai.aliyuncs.com/img/image-20211225002538241.png)