# Reactive Rule Engine

[![Build Status](https://travis-ci.org/UniCreditDnA/reactive-rule-engine.svg)](https://travis-ci.org/UniCreditDnA/reactive-rule-engine)

Drools streaming engine embedded in an Akka actor for reactive and scalable rule-based applications

## License
The Reactive Rule Engine is distributed under the terms of the Apache 2.0 license. See the ``LICENSE`` file for details.

## Building

Building the Reactive Rule Engine is easy! 
You just need:
* [Maven](https://maven.apache.org/) (version 3.3 or greater)
* JDK 1.8 (Oracle JDK 1.8 is suggested)

Just checkout the code and run the usual ``mvn clean install``.

## Usage
Stable versions of the artifact are available on [Maven Central](http://central.sonatype.org/). 
As with all Maven projects, you just need to declare the proper dependency in your ``pom.xml`` file:

```
<dependency>
    <groupId>eu.unicredit.dna</groupId>
    <artifactId>reactive-rule-engine</artifactId>
    <version>${reactive-rule-engine.version}</version>
</dependency>
```
