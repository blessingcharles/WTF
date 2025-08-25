# Notes


## Remote Debugging a burp extension

- Run the burp, with remote debugging enabled
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address="*:5005" -jar /home/th3h04x/opt/tools/enumerations/BurpSuiteCommunity/burpsuite_community.jar
```
[portswigger](https://portswigger.net/burp/documentation/desktop/extend-burp/extensions/creating/debugging)

- Configure the respected IDE to connect to the attached session
[StackOverflow Thread](https://stackoverflow.com/questions/21114066/attach-intellij-idea-debugger-to-a-running-java-process)

## Matching a recursive subdomain in the filter

- Examples
```
(^|\.)example\.com$, (^|\.)web-security-academy.net$
```