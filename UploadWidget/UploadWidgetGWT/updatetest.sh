#!/bin/sh
D=s3://test-dwo-nl/apps
cd target/UploadWidgetGWT
S=UploadWidget.css
aws --profile prod s3 sync --acl public-read --delete $S $D/$S
S=UploadWidgetGWT
aws --profile prod s3 sync --acl public-read --delete $S $D/$S

