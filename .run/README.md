# New running configuration directory 

## What's this?

This is a shared directory for running configuration, managed by the IntelliJ IDEA.
Traditional IDEA put these configuration into `.idea`. We wouldn't share this directory because some local settings
couldn't be reused by other. So we add it to `.gitignore` instead.

IDEA 2020.1 introduced a new running configuration on `.run` directory, this would be a good practice to share the
common used project related running configurations by using 2020.1.

So I create these files, they are the configs about testing, local running, mocking data configuration.
And are well tested by different developers on different platforms.  

## How to use these files?

Just bump your IntelliJ IDEA to 2020.1
