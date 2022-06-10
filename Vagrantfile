Vagrant.configure("2") do |config|
  config.vm.provider "virtualbox"
  config.vm.box = "debian/contrib-buster64"
  config.vm.network "forwarded_port", guest: 3306, host: 3306
  config.vm.network "forwarded_port", guest: 6379, host: 6379
  config.vm.synced_folder ".", "/vagrant"
  config.vm.provision "shell", inline: <<-shell
    if [ ! -f "/var/log/first.log" ]; then
        sed -i '/^SELINUX=/s/enforcing/disabled/' /etc/selinux/config
        timedatectl set-timezone Asia/Shanghai
        systemctl stop firewalld && systemctl disable firewalld
        apt-get -y remove docker docker-engine docker.io containerd runc && apt-get -y update && apt-get -y install ca-certificates curl gnupg lsb-release
        mkdir -p /etc/apt/keyrings
        curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
        echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
        apt-get -y update && apt-get -y install docker-ce docker-ce-cli containerd.io docker-compose-plugin
        systemctl start docker && systemctl enable docker
        docker run --name mysql -e MYSQL_USER=root -e MYSQL_PASSWORD=root -p 3306:3306 -d mysql:8
        docker run --name redis -p 6379:6379 -d redis:latest
        echo $(date) > /var/log/first.log
    fi
  shell
end
