#!/usr/bin/perl

use strict;
use warnings;
use threads;
use Data::Dumper;

#________________________________________________________________________________________
#
#    Initialization
#________________________________________________________________________________________

# File containing properties of script. He is supposed to be in current directory '.'.
my $PROPERTIES_FILE = "properties";


#
#     Declare global variables by trying to get them from $PROPERTIES_FILE.
#     If file or fields do not exist, default values are used.
#

my %properties  = read_properties ("properties");

# By default, this script is supposed to be in a subdirectory of the project 
# as <Local_dir>/deployment/start.pl
my $LOCAL_DIR       = $properties{"local_dir"}
                    ? $properties{"local_dir"}
                    : "../";
                
my $REMOTE_DIR      = $properties{"remote_dir"}
                    ? $properties{"remote_dir"}
                    : "~/BasicComponentModel/";
                
my $CONFIG_XML      = $properties{"config_xml"}
                    ? $properties{"config_xml"}
                    : "config.xml";
                
my $LOCAL_IP        = ($properties{"local_ip"}) 
                    ? ($properties{"local_ip"}) 
                    : getLocalIp (); 
                
my $COPY_FILES      = $properties{"copy_file"}
                    ? $properties{"copy_file"}
                    : 0;

# Define defaults files to transfere and add those presents in properties files
my $TRANSFERED_FILES =# "$LOCAL_DIR/jars/;$LOCAL_DIR/policies/;$LOCAL_DIR/config/;$LOCAL_DIR/$CONFIG_XML;
"$LOCAL_DIR/fr/";
$TRANSFERED_FILES   .=$properties{"transfered_files"}
                    ? $properties{"transfered_files"}
                    : "";

# If not defined, retrieve name of the user logged on this machine                
my $USER            = $properties{"username"}
                    ? $properties{"username"}
                    : $ENV{LOGNAME} || $ENV{USER} || getpwuid($<);

# Class path of components 
my $CP_GREGISTRY    = ($properties{"cp_gregistry"})
                    ? ($properties{"cp_gregistry"})
                    : "fr.upmc.components.registry.GlobalRegistry";
            
my $CP_CBARRIER     = ($properties{"cp_cyclicbarrier"})
                    ? ($properties{"cp_cyclicbarrier"})
                    : "fr.upmc.components.registry.DCVMCyclicBarrier";
            
my $CP_DCVM            = ($properties{"cp_dcvm"})
                    ? ($properties{"cp_dcvm"})
                    : "fr.upmc.components.registry.DistributedCVM";

# Construct the list of files to transfere
my @files = parseFilesToTransfere($TRANSFERED_FILES);

# Head of the command used to start components
my $start_cmd =     "java -cp '.:jars/jing.jar' ".
                    "-Djava.security.manager ".
                    "-Djava.security.policy=policies/dcvm.policy ";            


#
#     Handle script arguments
#

# Check if help is asked TODO
if ($#ARGV + 1 >= 1 && $ARGV[0] eq "?") {
    print "TODO";
}
# Else retrieved arguments to override 
else {
    # First argument is the user name used to connect to remote hosts
    if ($#ARGV + 1 >= 1) {
        $USER = ($ARGV[0] ne "default") ? $ARGV[0] : $USER;
    }
    # Second argument is local directory
    if ($#ARGV + 1 >= 2) {
        $LOCAL_DIR = ($ARGV[1] ne "default") ? $ARGV[1] : $LOCAL_DIR;
    }
    # Third argument is remote directory
    if ($#ARGV + 1 >= 3) {
        $REMOTE_DIR = ($ARGV[2] ne "default") ? $ARGV[2] : $REMOTE_DIR;
    }
    # Fourth and last argument indicates if we want to override files on remote hosts
    # "default" should not be met, but after all...
    if ($#ARGV + 1 >= 3) {
        if ($ARGV[3] !~ /^\s*(default|0|1)\s*$/) {
            print   "Abort : expected fourth argument must be 0 or 1.\n".
                    "Type 'start.pl ?' for more informations.\n";
            exit;
        }
        $COPY_FILES = ($ARGV[3] ne "default") ? $ARGV[3] : $COPY_FILES;
    }
    # Other arguments are simply ignored
}


#________________________________________________________________________________________
#
#    Instructions
#________________________________________________________________________________________

open (CONFIG_FILE, "<", $LOCAL_DIR.$CONFIG_XML) or die 
    "Cannot open $LOCAL_DIR$CONFIG_XML: $!.\n".
    "Maybe check the arguments or the properties file.\n".
    "You can also type 'start.pl ?' to get more informations.\n";

my %hosts     = (); # Store differents remote hosts.
my %utils     = (); # Store cyclic barrier and gregistry components
my %dcvms     = (); # Store dcms

#
#     Retrieve informations from $CONFIG_XML and put it into hashs
#
print "Parse $CONFIG_XML...";

my $file = "";
while (<CONFIG_FILE>) {
    $file .= $_;
}

# Retrieve all differents hostnames 
my @hosts_parsing = $file =~ /hostname="(.*?)".+?/gs;
foreach(@hosts_parsing) {
    $hosts{$_} = 1;
}

# Retrieve both cyclic barrier and global registry hosts
my @utils_parsing = ($file =~ /(cyclicBarrier|globalRegistry).+?hostname="(.*?)"/gs);
for(my $i = 0; $i < @utils_parsing; $i+=2) {
    $utils{$utils_parsing[$i + 1]}{$utils_parsing[$i]} = 1;
}

# Retrieve dcvms
my @jvm = $file =~/<jvms2hostnames>(.+?)<\/jvms2hostnames>/s;
my @dcvms_parsing = $jvm[0] =~/jvmuri="(.*?)".+?hostname="(.*?)"/gs;
for(my $i = 0; $i < @dcvms_parsing; $i+=2) {
    $dcvms{$dcvms_parsing[$i + 1]}{$dcvms_parsing[$i]} = 1;
}
print " Done !\n";

#
#     Copy files on remote hosts if needed
#
foreach my $host (keys %hosts) {
    if ($host ne $LOCAL_IP && $host ne "localhost") {
        # Check if files are missing on remote host
        system ("ssh", "$USER\@$host", "test", "-e", $REMOTE_DIR);
        my $missing_file = $? >> 8;
        
        # If files are missing or if copy is forced, let's copy files
        if ($missing_file || $COPY_FILES) {
            print "Exporting files on $host...\n";
            # Create needed directorys
            execute ($host, $USER, "    mkdir -p $REMOTE_DIR/jars; 
                                        mkdir -p $REMOTE_DIR/policies; 
                                        mkdir -p $REMOTE_DIR/config;");

            # Copy files 
            transfere ($host, $USER, @files);
        }
    }
}


#
#     Now, start components in separated threads
#

my @threads = ();

# First, start cyclic barrier and gregistry
foreach my $host (sort keys %utils) {
    foreach my $component (keys %{ $utils{$host} }) {
        start_component ($host, $USER, $component);
    }
}
# Then, start dcvms
foreach my $host (sort keys %dcvms) {
    foreach my $component (keys %{ $dcvms{$host} }) {
        start_component ($host, $USER, $component);
    }
}

foreach (@threads) {
    $_ -> join ();
}

exit;


#________________________________________________________________________________________
#
#    Functions
#________________________________________________________________________________________

#
#    This function start a component
#    - $host     : host where to copy files
#    - $user     : user name used to connect to remote host    
#    - $component: component name to start 
#        (if component is a dcvm, name passed as an argument is the name of the dcvm) 
#
sub start_component
{
    my ($host, $user, $component) = @_;
    my $command = "";
    
    if ($component eq "globalRegistry") {
        $command .= "$start_cmd $CP_GREGISTRY $CONFIG_XML";
    }
    elsif ($component eq "cyclicBarrier") {
        $command .= "$start_cmd $CP_CBARRIER $CONFIG_XML";
    }
    else {
         $command .= "$start_cmd $CP_DCVM $component $CONFIG_XML";
    }
    # If the component have to be executed on a remote machine, run it using ssh
    if ($host ne $LOCAL_IP && $host ne "localhost") {
        $command = "ssh $user\@$host \"cd $REMOTE_DIR && $command\"";
    }
    else {
        $command = "cd \"$LOCAL_DIR\" && $command";    
    }
    
    print "Start $component on $host...\n";
    print "\t$command\n\n";
    push (@threads, threads->create (sub { 
        system ('gnome-terminal', '-x', 'sh', '-c', $command); 
        #system (qw/xterm -e/,$command);
    }));

#    push (@threads, threads->create (sub { 
#        system ($command . " 1>/dev/null 2>/dev/null"); 
#    }));
}


#    TODO
#    This function return the local ip adress using ifconfig
#
sub getLocalIp
{
    #wget -qO- http://ipecho.net/plain ; echo
    my @ifconfig = `ifconfig`;
    my $first_found = 0;
    
    foreach (@ifconfig) {
        if (/(eth0|ppp0|wlan0)/) {
            $first_found = 1;
        }
        if (/\s*inet addr:([\d.]+)/ && $first_found) {
            $LOCAL_IP = $1;
            last;
        }
    }
    return $LOCAL_IP;
}


#
#    Return a hashmap as 'propertie => value' 
#     - $file : name of the properties file
#
sub read_properties
{
    my $file = shift;
    my %properties = ();

    if (open (PROPERTIES_FILE, "<", $PROPERTIES_FILE)) {
        while (<PROPERTIES_FILE>) {
            if ($_ =~ /^([^#].*?)\s*=\s*(.*?)$/) {
                $properties{$1} = $2;
            }
        }
    }
    return %properties;
}


#
# This function return a table of files to copy from a string
# containing this files separated by ;
#
sub parseFilesToTransfere 
{
    my $files = shift;
    my @files = ();
    
    while ($files =~ s/(.*?);//) {
        if ($1 ne "") {
            push @files, $1;
        }
    }
    if($files ne "") {
        push @files, $files;
    }

    return @files;    
}


#
#    This function transfere a list of file to remote host
#    - $host    : host where to copy files
#    - $user    : user name used to connect to remote host
#    - @files   : list of files
#
sub transfere 
{
    my ($host, $user, @files) = @_;
    system ("scp -r " . join (" ", @files) . " $user\@$host:$REMOTE_DIR");
}


#
#    This function execute a command to a remote host using ssh
#    To execute several commands, separate them with ';'
#    - $host    : host where to copy files
#    - $user    : user name used to connect to remote host
#    - $cmd     : command(s) to execute 
#
sub execute 
{
    my ($host, $user, $cmd) = @_;
    system ("ssh $user\@$host \"$cmd\"");
}
