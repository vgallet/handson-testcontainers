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

# pull two repositories
updateLocal()
{
    cd public/
    git pull origin master
    cd ../
    git pull origin master
}

# set git credential thanks to env variables into github url
setGitCredential()
{
    cd public/
    if [ ! "q$GITHUB_TOKEN" = "q" ] && [ ! "q$GITHUB_USER" = "q" ];
    then
        git remote set-url origin https://$GITHUB_USER:$GITHUB_TOKEN@github.com/Zenika/handson-testcontainers.git
    fi

    cd ../
    if [ -n $GITHUB_TOKEN ] && [ -n $GITHUB_USER ];
    then
        git remote set-url origin https://$GITHUB_USER:$GITHUB_TOKEN@github.com/$GITHUB_USER/handson-testcontainers.git
    fi
}

set -e         # -x print all command (to debug), -e exit at any command failure

$(setGitCredential)
$(updateLocal > /dev/null)

if [ ! -d "./node_modules" ]; then
    npm install --bin-links
fi
npm run build

cd public/
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
git submodule sync --recursive
git add ./public/

# skip travis to avoid travis build retry
git commit -m "[skip travis] Release public folder"
git push origin HEAD:master
