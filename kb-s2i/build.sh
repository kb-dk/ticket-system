cd /tmp/src

cp -rp -- /tmp/src/ticket-system-service/target/ticket-system-service-*.war "$TOMCAT_APPS/ticket-system-service.war"
cp -- /tmp/src/ticket-system-service/conf/ocp/ticket-system-service.xml "$TOMCAT_APPS/ticket-system-service.xml"

export WAR_FILE=$(readlink -f "$TOMCAT_APPS/ticket-system-service.war")

