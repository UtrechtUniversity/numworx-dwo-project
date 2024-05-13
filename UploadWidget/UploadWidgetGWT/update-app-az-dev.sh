set -ex
cd ../$1/target/$3
PATH=$PATH:/usr/local/bin
X=echo
X=
if test -f $2/$2.nocache.js
then
# BSD (MacOSX)
#	EXP=$(date -v+1d +%Y-%m-%d)
# posix (ubuntu)
#	EXP=$(date -d 'next day' +%Y-%m-%d)
. ~/aws.env
SAS=$(az storage container generate-sas --account-name numworxacc --name test  --auth-mode key  --permissions dlrw --expiry $EXP --account-key $KEY)
SAS=$(echo $SAS|tr -d '"')

azcopy copy $3.css https://numworxacc.blob.core.windows.net/test/apps/?"$SAS"
azcopy copy $2 https://numworxacc.blob.core.windows.net/test/apps/?"$SAS" --recursive=true






else
	echo $2 missing in $(pwd)
fi
