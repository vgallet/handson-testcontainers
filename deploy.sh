#!/bin/sh

getOs()
{
    case "$(uname -s)" in
        Darwin)
            echo 'macos'
            ;;
        Linux)
            echo 'linux'
            ;;
        CYGWIN*|MINGW32*|MSYS*)
            echo 'windows'
            ;;
        *)
            echo 'other'
            ;;
    esac
}

set -e         # -x print all command (to debug), -e exit at any command failure

if [ ! -d "./node_modules" ]; then
    npm install --bin-links
fi

npm run build

cd public/
if [ -n $GITHUB_TOKEN ] && [ -n $GITHUB_USER ];
then
    git remote set-url origin https://$GITHUB_USER:$GITHUB_TOKEN@github.com/Zenika/handson-testcontainers.git
fi
git pull origin master
git add -A

os=$(getOs)
if [ "$os" = "macos" ];
then 
    DATE=$(date -u "+%Y-%m-%dT%H:%M:%S%z"); 
elif [ "$os" = "linux" ];
then
    DATE=$(date -Iseconds);
else
    exit 1
fi;

git commit -m "Release $DATE"
git push origin HEAD:master

# return to parent repository
cd ../
if [ -n $GITHUB_TOKEN ] && [ -n $GITHUB_USER ];
then
    git remote set-url origin https://$GITHUB_USER:$GITHUB_TOKEN@github.com/RouxAntoine/handson-testcontainers.git
fi

git pull origin master
git submodule sync --recursive
git add ./public/

# skip travis to avoid travis build retry
git commit -m "[skip travis] Release public folder"
git push origin HEAD:master


