# 1. Mac 系统设置

触控板设置:

- **开启轻点点按**：“系统偏好设置-触控板-光标与点按-轻点来点按”
- **开启三指拖动**：“系统偏好设置-辅助功能-指针控制-触控板选项-启动拖移”
- **关闭触控板前进后退**: `系统设置 - 触控板 - 更多手势 - 在页面之间轻扫`，取消勾选

键位:

- **control 和 cap 互换**: 设置 -> 键盘 -> 修饰键

# 2. 日常软件安装



- 分屏: rectangle

- 剪切板: FastClip3

- 压缩解压: keka

- 视频播放: IINA

- PDF 阅读器: PDF export, Acrobat

- 菜单栏管理:  bartender

- 磁盘清理: 腾讯柠檬清理

- 快捷键提示: cheatsheet

- 截屏: snipaste

- 文本编译器: sublime, vscode, office三件套

- MD 编辑器: Typora
  - https://github.com/airyv/typora-theme-redrail
  
- 效率: alfred

- 下载: fdm, ndm, Downie4

- 电池保护: AIDente, coconuBattery

- 翻译: Bob

- 右键: iRightMouse

- 快速打开应用: Manico

  





# 3. 终端环境

## Homebrew

> https://zhuanlan.zhihu.com/p/90508170

1. 安装 Homebrew：

```sh
/bin/bash -c "$(curl -fsSL https://gitee.com/ineo6/homebrew-install/raw/master/install.sh)"
```

2. 环境变量设置

```shell
# 具体参照终端给出的 Next steps
echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> /Users/sls/.zprofile
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

## Git

个人信息配置

```sh
git config --global user.email "xxx"
git config --global user.name "xxx"
git config --global color.ui true
git config --global alias.co checkout  # 别名
git config --global alias.ci commit
git config --global alias.st status
git config --global alias.br branch
git config --global core.editor "vim"  # 设置Editor使用vim
git config --global core.quotepath false # 设置显示中文文件名
```

代理加快:

```sh
git config --global http.proxy 127.0.0.1:7890
git config --global https.proxy 127.0.0.1:7890
```

取消代理

```sh
git config --global --unset http.proxy
git config --global --unset https.proxy
```

查看配置

```shell
git config --global  --list
```





## vim

vundle管理插件

```sh
git clone https://github.com/VundleVim/Vundle.vim.git ~/.vim/bundle/Vundle.vim
```

```shell
syntax on

set number
set mouse=a
set autoindent
set smartindent
set cindent

set tabstop=4
set shiftwidth=4
set expandtab
set smarttab

set hlsearch

set showmatch
```

## zsh

1. 安装 oh-my-zsh

```shell
sh -c "$(curl -fsSL https://gitee.com/mirrors/oh-my-zsh/raw/master/tools/install.sh)"
```

2. 安装 zsh-autosuggestion 与 autojump：

```shell
# zsh-autosuggestion
brew install autojump
git clone https://github.com/zsh-users/zsh-syntax-highlighting.git $ZSH_CUSTOM/plugins/zsh-syntax-highlighting
git clone https://github.com/zsh-users/zsh-autosuggestions $ZSH_CUSTOM/plugins/zsh-autosuggestions
# 修改 ~/.zshrc文件
plugins=(git zsh-autosuggestions autojump zsh-syntax-highlighting)
source ~/.zshrc
```

3. 主题选取

```shell
git clone --depth=1 https://github.com/romkatv/powerlevel10k.git $ZSH_CUSTOM/themes/powerlevel10k
# 修改 ~/.zshrc文件
ZSH_THEME="powerlevel10k/powerlevel10k"
source ~/.zshrc
```

## npm

```shell
brew install npm
# 将npm的镜像源设置为淘宝镜像源
npm install -g cnpm --registry=https://registry.npm.taobao.org
```



## 图床

1. 安装 picgo - core

```shell
cnpm install picgo -g
# 验证安装是否成功
picgo -v
```

2. 安装上传插件

```shell
picgo set uploader
```

3. 修改配置文件: ~/.picgo/config.json
4. typora 配置

- 上传服务: 选择 Custom Command
- 命令:  /opt/homebrew/bin/node /opt/homebrew/bin/picgo upload

## ssh连接服务器

1. 在 mac 电脑生成ssh

```shell
cd ~/.ssh/
ssh-keygen
chmod 400 ~/.ssh/id_rsa
```

2. 创建配置文件

```shell
# ~/.ssh 目录下
vim config
# config文件
Host web1 # 给服务器ip起一个别名
Hostname xxxx # 服务器ip
User root # 服务器用户名
Port 22 # 服务器端口
IdentityFile ~/.ssh/id_rsa # 密钥
```

3. 将 mac 上的文件复制到服务器上

```shell
scp ~/.ssh/id_rsa.pub web1:/home/
```

4. 在服务器中进行文件迁移

```shell
cat /home/id_rsa.pub >> .ssh/authorized_keys
```

5. 测试

```shell
ssh web1
```











# 4. 开发环境

## JDK

> https://www.azul.com/downloads/?package=jdk
>
> 选 macOS ARM 64-bit v8 版本
>

多版本 JDK 切换: ~/.zshrc 进行修改

```shell
# JDK8
export JAVA_8_HOME="/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home"
alias jdk8='export JAVA_HOME=$JAVA_8_HOME'
# JDK18
export JAVA_18_HOME="/Library/Java/JavaVirtualMachines/zulu-18.jdk/Contents/Home"
alias jdk18='export JAVA_HOME=$JAVA_18_HOME'
# 默认 JDK8
export JAVA_HOME=$JAVA_8_HOME
```



## Maven

安装

```
brew install maven
```

查看版本

```
mvn -v
```

换源: 在 setting.xml 中进行修改

```xml
<!-- 
仓库配置
-->
<localRepository>/opt/homebrew/Cellar/maven/3.8.6/repository</localRepository>
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

- 修改 Maven home path, User setting file, local repository

## Mysql

# 5. IDEA 环境

插件:

- jclasslib
- Key Promoter X
- Rainbow Brackets
- translation

开发设置:

- 自动导包:
- 设置模版: 

## check-style

1. 下载 check-style 插件
2. 导入配置文件(目前存在 scratch 中)
3. 添加 git-hook: 需要放到`.git/hooks/` 目录，

```sh
#!/bin/sh
#set -x

echo "begin to execute hook"gst
mvn checkstyle:check

RESULT=$?

exit $RESULT
```

## 深拷贝

https://juejin.cn/post/7083384279123623966

https://github.com/rookie-ricardo/BeanMappingKey
