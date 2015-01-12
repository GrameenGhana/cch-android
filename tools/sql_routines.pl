#!/usr/bin/perl -s

# note the use of -s for switch processing. Under NT/2000, you will need to
# call this script explicitly with -s (i.e., perl -s script) if you do not
# have perl file associations in place. 
# -s is also considered 'retro', many programmers prefer to load
# a separate module (from the Getopt:: family) for switch parsing.

use Cwd; # module for finding the current working directory

my $path = "C:\\Users\\Grameen\\Documents\\Github\\cch-android\\assets\\www\\cch\\modules\\stayingwell\\templates\\gliving\\content\\routines";  
my $outfile = "C:\\Users\\Grameen\\Documents\\Github\\cch-android\\tools\\sql_out.sql";


# place contents in file
open(my $fh, '>', $outfile) or die "\nCould not open file $nf: $!";
print $fh "DROP TABLE IF EXISTS `stayingwell_routines`;\n".
          "CREATE TABLE stayingwell_routines (
	        id INT(11) NOT NULL AUTO_INCREMENT,
 	        tod VARCHAR(15) NOT NULL,
	        profile VARCHAR(15) NOT NULL,
	        plan VARCHAR(15) NOT NULL,
	        tip VARCHAR(255) NOT NULL,
	        `order` INT(11) NOT NULL, PRIMARY KEY(`id`)) ENGINE=Innodb DEFAULT CHARSET=utf8;\n";
print $fh "INSERT INTO stayingwell_routines (tod,profile,plan,tip,`order`) VALUES ";
close $fh;

my $lines = 1;

# This subroutine takes the name of a directory and recursively scans 
# down the filesystem from that point looking for files named "core"
sub ScanDirectory{
    my ($workdir) = shift; 
    my ($profile) = shift; 
    my ($plan) = shift; 

    my ($startdir) = &cwd; # keep track of where we began

    chdir($workdir) or die "Unable to enter dir $workdir:$!\n";
    opendir(DIR, ".") or die "Unable to open $workdir:$!\n";
    my @names = readdir(DIR) or die "Unable to read $workdir:$!\n";
    closedir(DIR);

    foreach my $name (@names){
        next if ($name eq "."); 
        next if ($name eq "..");

        if (-d $name){                  # is this a directory?
            $profile = ($profile eq "") ? $name : $profile;
            $plan = ($profile ne "") ? $name : $plan;
            &ScanDirectory($name, $profile, $plan);
            next;
        }

        if($name eq "afternoon.html" or $name eq "evening.html" or $name eq "morning.html") {
            my $l = $startdir;
            $l =~ s/.*?\/(naana|mary|michael)$/$1/g;
            print "\nProcessing $l, $plan, $name";
            &getItems($startdir."/$plan/$name", $l, $plan, $name);
            if ($name eq "morning.html") { $profile = ""; $plan = ""; }
        }

        chdir($startdir) or 
           die "Unable to change to dir $startdir:$!\n";
    }
}

sub getItems
{
    my $f = shift;
    my $profile = shift;
    my $plan = shift;
    my $tod = shift;
    $tod =~ s/\.html//g;

    open(FILE, "< $f") or die "Can't read file 'filename' [$!]\n";  
    my @document = <FILE>; 
    close (FILE);  

    my @routines;
    my $sql = "";
    my $order = 1;
    foreach my $line (@document) {
        if ($line =~ /<li>(.*?)<\/li>/g) {
            $sql = "";
            my $action = $1;
            $action =~ s/you've/you have/g;
            $action =~ s/'/\\'/g;
            $action =~ s/"/\\"/g;
            $action =~ s/<a.*?>(.*?)<\/a>/$1/g;
            $sql .=  "('$tod', '$profile', '$plan', '$action', $order)";
            push @routines, $sql;
            $order++;
        }
    }

    # place contents in file
    open(my $fh, '>>', $outfile) or die "\nCould not open file $nf: $!";
    print $fh join ", ", @routines;
    close $fh;
}


&ScanDirectory("$path\\mary","","");     
&ScanDirectory("$path\\michael","","");     
&ScanDirectory("$path\\naana","","");     
