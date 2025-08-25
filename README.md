# WTF - Where's The Flaw

Simple automated burp extension to scan for various vulnerabilities in community edition. 

## Objective
Develop a burp plugin while solving each labs, which would be able to scan request and give potential issue, similar to the thing available in the paid version.

## Description

### Modules

##### Scanner

- CoreScanner
The scanner should be able to iterate the request through all the scanners and the corresponding
scanners should be able to analyze the response

#### General Features

- Add scope with regex domain match to apply scan only to those scoped targets
- Collect web dir paths and store them as cacheable and non-cacheable ones.

---
## Client Side

---
## Server Side


### Web Cache Deception

1. Random Suffix Detection
![Random Suffix Images](https://github.com/blessingcharles/WTF/blob/main/docs/images/Wcd_Random_Suffix.png)
[Portswigger Lab](https://portswigger.net/web-security/web-cache-deception/lab-wcd-exploiting-path-mapping)
Redirect the user to the flawed url suffix and make the cache system to cache it. Save and deliver the
exploit url to the victim
```
HTTP/1.1 302 Found
Location: https://0a530024039aba0a8068aec800b4002d.web-security-academy.net/my-account/test1.js
```
2. delimiter discrepancy
[PortSwigger Lab](https://portswigger.net/web-security/web-cache-deception/lab-wcd-exploiting-path-delimiters)
3. path normalization discrepancy between cache and origin server

*Only Origin Server is Normalizing*
- build a list of static wordlist specific to the scope and its cacheable. eg: /assets/js/
- fuzz that collected paths in uncacheable paths like 
eg: /account/profile --> /account/assets/js/..%2f..%2fprofile


---
### References

1. [burp extensions example](https://github.com/PortSwigger/burp-extensions-montoya-api-examples)
2. [burp api doc](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/index.html)
3. 