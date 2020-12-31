Vagrant.configure("2") do |config|
  config.vm.box = "centos8"
  config.vm.network "forwarded_port", guest: 8081, host: 8081, host_ip: "127.0.0.1"
  config.vm.network "forwarded_port", guest: 3306, host: 3306, host_ip: "127.0.0.1"
  config.vm.network "private_network", ip: "192.168.33.10"
  config.vm.provision "shell", privileged: false, inline: <<-SHELL
    if [ -z "$(rpm -aq | grep mysql-server)" ]; then
        sudo yum -y update && sudo yum -y upgrade
        sudo yum -y localinstall https://dev.mysql.com/get/mysql80-community-release-el8-1.noarch.rpm && sudo yum -y update
        sudo yum -y install mysql-server
        sudo systemctl start mysqld
        sudo systemctl enable mysqld
        wget -O /tmp/schema.sql https://github.com/bluef1ash/phone-number-manager/raw/master/src/main/resources/schema.sql
        wget -O /tmp/data.sql https://github.com/bluef1ash/phone-number-manager/raw/master/src/main/resources/data.sql
        mysql -uroot < /tmp/schema.sql
        mysql -uroot < /tmp/data.sql
        mysql -uroot << EOF
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
EOF
    fi
  SHELL
end
