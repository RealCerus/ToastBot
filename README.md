# Toast Bot
Toast Bot is a neat Discord bot that brings a little bit of fun to your guild.

## Features
The bot features a few commands (and more are to come):

- help
- info
- credits
- bot-channel
- set-prefix
- toast
- toastify
- cat-gif

The default command prefix is `+`.\
The `toast` command will generate an image like this:\
![Image](https://img.cerus-dev.de/toast_cmd_prev.png)\
The `toastify` command will generate an image like this:\
![Image](https://img.cerus-dev.de/toastify_cmd_prev.png)\

## Setup
You can find the bot at DiscordBots.org: [link here]
\
There's also a self hosted version, but we'll talk about that later on.
\
\
After you invited the bot to your guild you need to set one (or more) bot channels in order to use the commands.
Type `+bot-channel add` in a text channel of your choice to register it as a bot channel. You can type `+bot-channel` remove to unregister it.
\
You are also able to change the command prefix for your guild. Just type `+set-prefix <new prefix>` in the previously registered bot channel to change it.

## Self hosted bot
You can download the latest release on the release tab and run it in the command line with `java -jar ToastBot-VERSION.jar`. Notice that you need to change the filename at the end of the command to the filename of the downloaded file. You may also add different arguments.
