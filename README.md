# Note
This project is very old and it contains a lot of bad programming. Use at your own risk



![Discord](https://img.shields.io/discord/565825337108463626.svg?style=flat-square) ![GitHub commit activity](https://img.shields.io/github/commit-activity/m/RealCerus/ToastBot.svg?style=flat-square) ![GitHub issues](https://img.shields.io/github/issues/RealCerus/ToastBot.svg?style=flat-square) [![Build Status](https://ci.lukassp.de/job/Cerus-ToastBot/badge/icon?style=flat-square)](https://ci.lukassp.de/job/Cerus-ToastBot/)

# Toast Bot
Toast Bot is a neat Discord bot that brings a little bit of fun to your guild.\
Invite the bot now: [Click here](https://discordapp.com/oauth2/authorize?client_id=565579372128501776&permissions=125952&scope=bot)\
Join the support Discord: [Click here](https://discord.gg/ddX3eSf)\
[![Discord Bots](https://discordbots.org/api/widget/565579372128501776.svg)](https://discordbots.org/bot/565579372128501776)

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
- economy
- search-gif
- vote

The default command prefix is `+`.\
The `toast` command will generate an image like this:\
![Image](https://img.cerus-dev.de/toast_cmd_prev.png)\
The `toastify` command will generate an image like this:\
![Image](https://img.cerus-dev.de/toastify_cmd_prev.png)\
The `search-gif` command will search for 5 gifs matching your search query.

## Help
See the [FAQ](https://github.com/RealCerus/ToastBot/blob/master/FAQ.md)

## Setup
You can find the bot at DiscordBots.org: [Click here](https://discordbots.org/bot/565579372128501776)
\
There's also a self hosted version, but we'll talk about that later on.
\
\
After you invited the bot to your guild you need to set one (or more) bot channels in order to use the commands.
Type `+bot-channel add` in a text channel of your choice to register it as a bot channel or put "[Bot Channel]" in the channel topic / description. You can type `+bot-channel remove` to unregister it.
\
You are also able to change the command prefix for your guild. Just type `+set-prefix <new prefix>` in the previously registered bot channel to change it.

## Self hosted bot
Latest version: [![Build Status](https://ci.lukassp.de/job/Cerus-ToastBot/badge/icon?style=flat-square)](https://ci.lukassp.de/job/Cerus-ToastBot/)\
You can download the latest release on the release tab or on [Jenkins](https://ci.lukassp.de/job/Cerus-ToastBot/) and run it in the command line with `java -jar ToastBot-VERSION.jar`. Notice that you need to change the filename at the end of the command to the filename of the downloaded file. You may also add different arguments.
