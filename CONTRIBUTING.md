# How to report issues
Use the OW2 JIRA: https://jira.ow2.org/browse/AUTHZFORCE/component/12021

# How to make a release (for committers only)
 
1. From the develop branch, run: `$ mvn jgitflow:release-start`
1. Update the CHANGELOG according to keepachangelog.com.
1. When done, run: `$ mvn jgitflow:release-finish`
1. Connect and log in to the OSS Nexus Repository Manager: https://oss.sonatype.org/
1. Go to Staging Profiles and select the pending repository authzforce-*... you just uploaded with `jgitflow:release-finish`
1. Click the Release button to release to Maven Central.