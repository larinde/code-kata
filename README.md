```bash
#Create parent project
mvn archetype:generate -DgroupId=com.koweg.kata -DartifactId=code-kata -Dversion=1.0.0-SNAPSHOT -DpackageName=com.koweg.kata -DarchetypeGroupId=org.codehaus.mojo.archetypes -DarchetypeArtifactId=pom-root -DarchetypeVersion=1.1 -DinteractiveMode=false
#Create child project in parent
mvn archetype:generate -DarchetypeGroupId=org.jetbrains.kotlin -DarchetypeArtifactId=kotlin-archetype-jvm -DkotlinVersion=1.3.70 -DartifactId=kata16-business-rules  -DgroupId=com.koweg.kata -Dversion=1.0.0-SNAPSHOT -Dpackage=com.koweg.kata

```

* [Business Rules Kata](http://codekata.com/kata/kata16-business-rules/)

###### Analysis

- diverse and increasing set of business rules
- a commonality in all rules is a _**condition**_ and a consequent _**action**_
  -  an _**action may imply/trigger another _**condition**_; leading to a chain of business rules
- A possible solution would be a switch construct.
  - but that would require a loop to iterate a linked sequence of rules.
  - besides that the switch - case construct grows in proportion to number of rules.
- A cleaner solution would be to apply the ** Chain of Responsibility ** pattern:
  - This enables decoupling the sender/trigger ( _**condition**_) from the receiver (action)
  - It also enables composition of chained dependent components ( _**conditions**_ and _**actions**_)
    - Each rule in the composition only needs to know its immediate dependant.
    - The last rule in the chain accepts a **terminal rule** that ends control flow in a rule pipeline.  
  - A complex set of business rules can be simply expressed as a process pipeline of linked _**rule nodes**_.
