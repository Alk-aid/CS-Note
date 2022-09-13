# 1. Mac 系统设置

触控板设置:

- **开启轻点点按**：“系统偏好设置-触控板-光标与点按-轻点来点按”
- **开启三指拖动**：“系统偏好设置-辅助功能-指针控制-触控板选项-启动拖移”
- **关闭触控板前进后退**: `系统设置 - 触控板 - 更多手势 - 在页面之间轻扫`，取消勾选

键位:

- **control 和 cap 互换**: 设置 -> 键盘 -> 修饰键

修改用户名:



# 2. 日常软件安装

## 2.1 Alfred

## 2.2 Typora

主题: https://github.com/Theigrams/My-Typora-Themes

## 2.3 snipaste

## 2.4 cheatsheet



# 3. 开发软件

## 3.1 Homebrew

> https://zhuanlan.zhihu.com/p/90508170

1. 安装 Homebrew：

```sh
/bin/bash -c "$(curl -fsSL https://gitee.com/ineo6/homebrew-install/raw/master/install.sh)"
```

2. 环境变量设置

```shell
# 具体参照终端给出的 Next steps
echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> /Users/sunyueshuai/.zprofile
eval "$(/opt/homebrew/bin/brew shellenv)"
```

3. 换源:

```shell
git -C "$(brew --repo)" remote set-url origin https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/brew.git

git -C "$(brew --repo homebrew/core)" remote set-url origin https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/homebrew-core.git

git -C "$(brew --repo homebrew/cask)" remote set-url origin https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/homebrew-cask.git

brew update
```

4. 安装的软件位于: /opt/homebrew/Cellar

- 通过brew install安装应用最先是放在/opt/homebrew/Cellar/目录下
- 有些应用会自动创建软链接放在/usr/bin或者/usr/sbin，同时也会将整个文件夹放在/usr/local
- 查找在homebrew安装软件的路径: brew list xxx

## iTerm2

安装

```shell
brew cask install iterm2
```

## vim

## zsh

1. 安装 oh-my-zsh

```shell
sh -c "$(curl -fsSL https://raw.github.com/robbyrussell/oh-my-zsh/master/tools/install.sh)"	
```

2. 安装 zsh-autosuggestion 与 autojump：

```shell
# zsh-autosuggestion
brew install autojump
git clone https://github.com/zsh-users/zsh-syntax-highlighting.git $ZSH_CUSTOM/plugins/zsh-syntax-highlighting
git clone https://github.com/zsh-users/zsh-autosuggestions $ZSH_CUSTOM/plugins/zsh-autosuggestions

plugins=(git zsh-autosuggestions autojump zsh-syntax-highlighting)
source ~/.zshrc
```

3. 主题选取

```shell
git clone --depth=1 https://github.com/romkatv/powerlevel10k.git $ZSH_CUSTOM/themes/powerlevel10k
# 修改 THEME
ZSH_THEME="powerlevel10k/powerlevel10k
source ~/.zshrc
```





## git

个人信息配置



代理加快:

```
git config --global http.proxy 127.0.0.1:7890
git config --global https.proxy 127.0.0.1:7890
```

取消代理

```
git config --global --unset http.proxy
git config --global --unset https.proxy
```



# 4. Java环境

## 4.1 JDK

> https://www.azul.com/downloads/?package=jdk
>
> 选 macOS ARM 64-bit v8 版本
>
> 或者使用
>
> brew install openjdk

## 4.2 Maven

安装

```
brew install maven
```

解决无 JAVA_HOME 问题

```
vim ~/.zshrc
export JAVA_HOME=$(/usr/libexec/java_home)
source ~/.zshrc
```

查看版本

```
mvn -v
```

换源

```xml
<!-- 
仓库配置
-->
<localRepository>/usr/local/apache-maven-3.8.6/repo</localRepository>
<!-- 
换源, 注释掉默认的Maven源，新增aliyun的源
-->
<mirror>
  <id>aliyunmaven</id>
  <mirrorOf>*</mirrorOf>
  <name>aliyunpublic</name>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

IDEA 配置 Maven



