Vagrant.configure("2") do |config|
  config.vm.box = "centos8"
  config.vm.network "forwarded_port", guest: 3306, host: 3306
  config.vm.network "forwarded_port", guest: 6379, host: 6379
  config.vm.synced_folder ".", "/vagrant"
  config.vm.provision "shell", inline: <<-shell
    if [ ! -f "/var/log/first.log" ]; then
        sed -i '/^SELINUX=/s/enforcing/disabled/' /etc/selinux/config
        timedatectl set-timezone Asia/Shanghai
        systemctl stop firewalld
        systemctl disable firewalld
        if [ ! -f "/etc/yum.repos.d/Centos-8.repo" ]; then
            wget -O /etc/yum.repos.d/Centos-8.repo http://mirrors.aliyun.com/repo/Centos-8.repo
            yum clean all && yum makecache
            yum -y upgrade && yum -y update
        fi
        if [ -z "$(rpm -aq | grep java-11-openjdk)" ]; then
            yum -y update && yum -y upgrade
            yum -y install java-11-openjdk java-11-openjdk-devel
        fi
        if [ -z "$(rpm -aq | grep mysql-server)" ]; then
            yum -y update && yum -y upgrade
            yum -y localinstall https://dev.mysql.com/get/mysql80-community-release-el8-1.noarch.rpm
            yum -y install mysql-server
            systemctl start mysqld
            systemctl enable mysqld
            mysql -u root < /vagrant/backend/src/main/resources/schema.sql
            mysql -u root << EOF
                ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
                CREATE USER 'root'@'%' IDENTIFIED BY 'root';
                GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
                FLUSH PRIVILEGES;
EOF
        fi
        if [ -z "$(rpm -aq | grep redis)" ]; then
            yum -y update && yum -y upgrade
            yum -y install redis
            sed -i 's/^[#\s]\?bind\s127\.0\.0\.1/bind 0.0.0.0/' /etc/redis.conf
            systemctl start redis
            systemctl enable redis
        fi
        echo $(date) > /var/log/first.log
    fi
  shell
end
