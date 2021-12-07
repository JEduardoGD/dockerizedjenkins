#!groovy
 
import jenkins.model.*
import hudson.security.*
import jenkins.security.s2m.AdminWhitelistRule
import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.security.s2m.AdminWhitelistRule

def instance = Jenkins.getInstance()
 

//
// Automate Admin Setup & Plugin Installs

def user = new File("/tmp/user").text.trim()
def pass = new File("/tmp/pass").text.trim()
 
// Create Admin User
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount(user, pass)
instance.setSecurityRealm(hudsonRealm)

// Set Auth to Full Control Once Logged In
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
instance.setAuthorizationStrategy(strategy)


//
// Lock Down Jenkins Security
 
instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)

// Disable remoting
instance.getDescriptor("jenkins.CLI").get().setEnabled(false)

// Enable Agent to master security subsystem
instance.injector.getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false);

// Disable jnlp
instance.setSlaveAgentPort(-1);

//  CSRF Protection
instance.setCrumbIssuer(new DefaultCrumbIssuer(true))

// Disable old Non-Encrypted protocols
HashSet<String> newProtocols = new HashSet<>(instance.getAgentProtocols());
newProtocols.removeAll(Arrays.asList(
        "JNLP3-connect", "JNLP2-connect", "JNLP-connect", "CLI-connect"
));
instance.setAgentProtocols(newProtocols);

instance.save()
