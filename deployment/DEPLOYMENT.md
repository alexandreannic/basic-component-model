# Deployment instruction

### Presentation

The script `start.pl` is a part of the **BasicComponentModel** project.
It deploys a set of components on several JVM and eventually on several hosts.
Deployment configuration can be found in `/BasicComponentModel/config.xml`.

By default, this script and the properties file must be located in a subdirectory of the 
**BasicComponentModel** root. You can edit both location and `config.xml` name through the properties file.

Below, the expected BasicComponentModel directory structure.
```
 BasicComponentModel
	|_ jars/
	|_ policies/
	|_ config/
	|_ config.xml
	|_ deployment/
  		|_ start.pl
  		|_ properties

```

### Properties file

Although the main arguments can be passed directly as a parameter of the script, it may be necessary to create a
configuration file for the optional arguments.
 
#### File structure

Fields and values must be separated by `=`. If a field does not exist or is empty, default values will 
be used. Lines starting with `#` will be ignored.

#### Parameters

- `config_xml`: Config file name
- `local_dir`: Local directory where is stored the project
- `remote_dir`: Remote directory where the necessary files will be uploaded
- `username`: User name used to connect to remote machine through SSH
- `local_ip`: Address ip of the local machine
- `copy_file`: If set to 0, existing remote files won't be overridden
- `cp_gregistry`: Class path of the **global registry** component
- `cp_cyclicbarrier`: Class path of the **cyclic barrier** component
- `cp_dcvm`: Class path of the **dcvm** component
- `transfered_files`: List of files to copy on remote hosts separated by a semicolon

	
#### Example

	# Name of the config file
	config_xml = config.xml

	# Local directory where is stored the project
	local_dir = 

	# Remote directory where the necessary files will be uploaded
	remote_dir = ~/BasicComponentModel/

	# User name will be used to connect to remote machine via ssh
	username = 

	# Adress ip of the local machine. 
	local_ip = 192.168.0.14

	# Class path of components
	cp_gregistry = fr.upmc.components.registry.GlobalRegistry
	cp_cyclicbarrier = fr.upmc.components.cvm.utils.DCVMCyclicBarrier
	cp_dcvm = fr.upmc.components.examples.basic_cs.DistributedCVM

	# You can force copying files by setting this to 1. 
	# If files are already stored, they will be overwritted
	copy_file = 1
	
	# Define files to copy on remote hosts. 
	# Path have to be defined from local_dir
	# To define severals files, separate them with ;
	# The following files/directorys will always be copied :
	# - jars/
	# - policies/
	# - config/
	# - config.xml
	transfered_files = bash-scripts/deploy-sources;bash-scripts/start-dcvm;
	
### Script parameters

The script accept several parameters that override some of the config files parameters. Theses arguments are 
respectively:
 
1. `username`
2. `local_dir`
3. `local_dir`
4. `copy_file`

It is possible to specify an argument without specifying the previous by using the keyword "default". 

The example below show ow to run the script by only specifying the remote directory.

```
./start.pl default default ~/new_remote_dir
```
