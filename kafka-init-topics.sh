/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server kafka:29092 --list

echo -e 'Creating kafka topics'
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server  kafka:29092 --create --if-not-exists --topic driver-info-events --partitions 3 --replication-factor 1
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server  kafka:29092 --create --if-not-exists --topic ride-info-events --partitions 3 --replication-factor 1
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server  kafka:29092 --create --if-not-exists --topic driver-status-events --partitions 3 --replication-factor 1
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server  kafka:29092 --create --if-not-exists --topic payment-method-details --partitions 3 --replication-factor 1
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server  kafka:29092 --create --if-not-exists --topic card-default-status-details --partitions 3 --replication-factor 1
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server  kafka:29092 --create --if-not-exists --topic new-passenger-details --partitions 3 --replication-factor 1
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server  kafka:29092 --create --if-not-exists --topic new-card-details --partitions 3 --replication-factor 1
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server  kafka:29092 --create --if-not-exists --topic removed-passenger-details --partitions 3 --replication-factor 1
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server  kafka:29092 --create --if-not-exists --topic error-card-details --partitions 3 --replication-factor 1
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server  kafka:29092 --create --if-not-exists --topic payment-result --partitions 3 --replication-factor 1
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server  kafka:29092 --create --if-not-exists --topic ride-payment-events --partitions 3 --replication-factor 1

echo -e 'Successfully created the following topics:'
/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server kafka:29092 --list