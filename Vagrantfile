Vagrant.configure("2") do |config|
  config.vm.box = "centos8"
  config.vm.network "forwarded_port", guest: 3306, host: 3306
  config.vm.network "forwarded_port", guest: 6379, host: 6379
  config.vm.synced_folder ".", "/vagrant"
  config.vm.provision "shell", inline: <<-shell
    if [ ! -f "/var/log/first.log" ]; then
        sed -i '/^SELINUX=/s/enforcing/disabled/' /etc/selinux/config
        timedatectl set-timezone Asia/Shanghai
        systemctl stop firewalld && systemctl disable firewalld
        yum install -y yum-utils device-mapper-persistent-data lvm2
        yum-config-manager --add-repo http://mirrors.aliyun.com/repo/Centos-8.repo
        yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
        yum -y localinstall https://dev.mysql.com/get/mysql80-community-release-el8-2.noarch.rpm
        yum clean all && yum makecache && yum -y upgrade && yum -y update
        yum -y install java-11-openjdk java-11-openjdk-devel
        yum -y install mysql-server mysql-devel
        systemctl start mysqld && systemctl enable mysqld
        mysql -u root < /vagrant/backend/src/main/resources/schema.sql
        mysql -u root << EOF
            ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
            CREATE USER 'root'@'%' IDENTIFIED BY 'root';
            GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
            FLUSH PRIVILEGES;
EOF
        yum -y install redis
        sed -i 's/^[#\s]\?bind\s127\.0\.0\.1/bind 0.0.0.0/' /etc/redis.conf
        systemctl start redis && systemctl enable redis
        yum remove -y docker docker-client docker-client-latest docker-common docker-latest docker-latest-logrotate docker-logrotate docker-engine
        yum install -y docker-ce docker-ce-cli containerd.io
        systemctl start docker && systemctl enable docker
        echo $(date) > /var/log/first.log
    fi
  shell
end
