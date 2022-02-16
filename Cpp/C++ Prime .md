

# C++ Prime

## 结构体

​	1.可以使用赋值运算符将结构赋给另一个同类型的结构

## 共用体

1.能够储存不同的数据类型，但是只能同时储存其中的一种类型。

## new和delete

1.不要使用delete来释放不是new分配的内存

​	

```C++
//这样是被允许的
int *ps = new int
int *pd=ps;
delete pd;
```

2.不要使用delete来释放同一个内存块两次

3.如果使用new[]为数组分配内存，则应使用delete[]来释放

4.如果使用new为一个实体分配内存，则应该使用delete（没有方括号）来释放

5.对空指针应用delete是安全的

## cctype

1.isupper 和 islower 和 isdigit真则返回true

2.tolower 和toupper

## 函数和二维数组

​	传入的参数应该是指针数组,形如ar2[] [4],(*ar2)[4],后面的4不可少

## 声明和定义

1.只允许进行一次定义，但是可以多次声明

2.extern用在变量或者函数的声明前，用来说明“此变量/函数是在别处定义的，要在此处引用”。extern声明不是定义，即不分配存储空间。也就是说，在一个文件中定义了变量和函数， 在其他文件中要使用它们， 可以有两种方式：使用头文件，然后声明它们，然后其他文件去包含头文件；在其他文件中直接extern。

## const

1.const修饰的变量本质上还是变量，只不过不能用变量本身去修改他，可以用其他的方法去修改他

**const其实只是一个对变量写权限的设定，并不代表某个变量永远不可能被修改。**

2.const对象必须初始化

3.定义为const后，编译器遇到该变量都会自动替换成常量

3.默认状态下，const对象只在本文件内有效，多文件内出现同名的const变量，其实是互相独立的，要想共享，需要不管是声明还是定义都加extern

​	**非const的定义默认为extern**

4.

```
const int a=5;
cout<<++a*2;
//a*2仍然为常量   
```

5.指向常量的指针叫做常量指针	const int *p	指向的值不可以修改

6.指针是常量的叫做指针常量 int *contst p	指针的指向不可以修改

7.顶层const 该对象是常量，底层const 指针或者引用所指的对象是常量

8.执行对象的拷贝时，顶层const和底层const有区别

​		对于顶层const 没什么影响

​		对于底层const 拷入和拷出的对象必须都具备底层const，或者可以转换过去，一般来说变量可以转化为常量，反之不行

## string

1.istream& getline ( istream &is , string &str , char delim ); delim是结束条件，默认为换行

2.s.size 的类型为string::size_type 是无符号型的

3.处理string中的字符，加上引用就可以修改string的字符

![image-20200926100302421](C:\Users\86191\AppData\Roaming\Typora\typora-user-images\image-20200926100302421.png)

![image-20200926100324260](C:\Users\86191\AppData\Roaming\Typora\typora-user-images\image-20200926100324260.png)

## vector

1.vector是一个类模板

2.但凡使用了迭代器 都不要进行填充元素操作，因为会让迭代器失效

## 数组

1.数组也可以使用begin和end函数

```
int *p1=begin(arr),*p2=end(arr);
```

## 表达式

### 1.左值与右值

​	当一个对象被用作右值的时候，用的是对象的值(内容),

​	当一个对象被用作左值的时候，用的是对象的身份(在内存中的位置)

​	如果表达式的求值是一个左值，decltype作用于该表达式得到的是一个引用类型

## 函数

1.用实参初始化形参时会忽略顶层const

2.下面的例子不算函数重载 会报错，判断能不能重载 主要看 编译器能否分清调哪个函数

```
void fcn(const int i){}
void fcn(int i){}
```

3.没有引用的数组 ，只有数组的引用 int(&arr)[10]=a

​	作用:普通的是传递了一个指针的副本

4.二维数组传参，传的就是**指向数组首元素的指针**，而数组首元素是一个数组，所以指针就是一个指向数组的指针

​	https://blog.csdn.net/DoasIsay/article/details/48316691?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.channel_param&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.channel_param

```
int *matrix[10]; //10个指针构成的数组
int (*matrix)[10]；//指向含有10个整数的数组的指针
```

5.

## 类

1.常对象只能调用常成员函数

​	普通对象可以调用全部成员函数

​	**常成员函数本质上就是使this指针变为const Person* const this; (底层和顶层指针)**

2.类的作用域

函数的返回类型通常都出现在函数名之前。因此当成员函数定义在类的外部时，返回类型中使用的名字都位于类的作用域之外，这时必须指明返回类型他是哪个类的成员

3.编译器处理完类中的全部声明后才会处理成员函数的定义

​	所以函数体可以使用类中定义的任何名字

4.某些const或者引用必须初始化，而且使用列表初始化，不能使用先定义后赋值