###########################################################
#ENV VARS
###########################################################
envValue=$1
APP_NAME=$2
GRAD_NAMESPACE=$3
COMMON_NAMESPACE=$4
BUSINESS_NAMESPACE=$5
SPLUNK_TOKEN=$6

SPLUNK_URL="gww.splunk.educ.gov.bc.ca"
FLB_CONFIG="[SERVICE]
   Flush        1
   Daemon       Off
   Log_Level    info
   HTTP_Server   On
   HTTP_Listen   0.0.0.0
   Parsers_File parsers.conf
[INPUT]
   Name   tail
   Path   /mnt/log/*
   Exclude_Path *.gz,*.zip
   Parser docker
   Mem_Buf_Limit 20MB
[FILTER]
   Name record_modifier
   Match *
   Record hostname \${HOSTNAME}
[OUTPUT]
   Name   stdout
   Match  absolutely_nothing_bud
   Log_Level    off
"
PARSER_CONFIG="
[PARSER]
    Name        docker
    Format      json
"
###########################################################
#Setup for config-maps
###########################################################
echo Creating config map "$APP_NAME"-config-map
oc create -n "$GRAD_NAMESPACE"-"$envValue" configmap "$APP_NAME"-config-map \
  --from-literal=GRAD_REPORT_API="http://educ-grad-report-api.$GRAD_NAMESPACE-$envValue.svc.cluster.local:8080/" \
  --from-literal=GRAD_STUDENT_API="http://educ-grad-student-api.$GRAD_NAMESPACE-$envValue.svc.cluster.local:8080/" \
  --from-literal=APP_LOG_LEVEL="ERROR" \
  --from-literal=REPORT_API"http://educ-grad-report-api.$GRAD_NAMESPACE-$envValue.svc.cluster.local:8080/" \
  --from-literal=GRAD_GRADUATION_REPORT_API="http://educ-grad-graduation-report-api.$GRAD_NAMESPACE-$envValue.svc.cluster.local:8080/" \
  --from-literal=PEN_API="http://student-api-master.$COMMON_NAMESPACE-$envValue.svc.cluster.local:8080/" \
  --from-literal=MAX_RETRY_ATTEMPTS="3" \
  --from-literal=GRAD_GRADUATION_API="http://educ-grad-graduation-api.$GRAD_NAMESPACE-$envValue.svc.cluster.local:8080/" \
  --from-literal=KEYCLOAK_TOKEN_URL="https://soam-$envValue.apps.silver.devops.gov.bc.ca/" \
  --from-literal=GRAD_STUDENT_GRADUATION_API="http://educ-grad-student-graduation-api.$GRAD_NAMESPACE-$envValue.svc.cluster.local:8080/" \
  --dry-run=client -o yaml | oc apply -f -

echo Creating config map "$APP_NAME"-flb-sc-config-map
oc create -n "$GRAD_NAMESPACE"-"$envValue" configmap "$APP_NAME"-flb-sc-config-map \
  --from-literal=fluent-bit.conf="$FLB_CONFIG" \
  --from-literal=parsers.conf="$PARSER_CONFIG" \
  --dry-run=client -o yaml | oc apply -f -
