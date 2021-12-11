# Design Document

## Context and Scope

The main purpose of the system is to provide FICT entrants with
the smooth experience of applying for studying and filling all necessary information
and the channel of communication with the Application Committee.

For now, there are two options for applying to FICT:

1. Physically go to Kyiv and bring all needed documents and fill forms;
2. Send documents to KPI Application Committee via email.

First approach is time-consuming and creates large queues every year.
It is especially critical nowadays with COVID-19 pandemic.
It's both dangerous and non-comfortable.

Second approach is acceptable in general but has significant downsides.
It is not formalized enough, and it is common that applicants miss
some important documents (like lists of priorities) just because of lack
of feedback and communication from KPI Application Committee. It is way less
popular than first approach because nobody really knows what to do and how to send docs, and,
what's even more important, nobody can be sure that all docs are correct, and they don't have to
send something more.

Also, there are particular documents that cannot be obtained by entrants themselves because
of the features of Ukrainian electronic application system. For example,
applicants that apply for a paid tuition can't receive documents from their
personal cabinet and need to communicate with the Application Committee for providing it
(we have no idea why does it work like this). They need to take a quest of finding Committee's contacts,
ask them for documents, signing and sending them back.

## Goals and non-goals

### Goals

* Provide unified interface for filling all needed forms and documents;
* Allow document signing with electronic signatures;
* Provide overview of application status;
* Provide instruments for approving applications and providing feedback if needed.

### Non-goals

* Electronic queues for physical application;
* Automated analysis and verification of documents.

## The actual design

### APIs

For user, system must provide endpoints for authorization, creating application,
providing signed documents, checking status.

For Application Committee operators system must provide endpoints for receiving
applications, changing their status (approved, rejected, pending), sending
feedback to users.

### Data storage

System must store information about users, operators, their messages(feedback), 
application statuses, documents.

Documents will be stored as files with the path in the db alongside with their master-key encrypted unique encryption keys.

## Alternatives considered

The very basic solution for given problem would be just a front-end SPA where user
can upload documents, sign them and send to Application Committee's email. This solves
some problems of existing approaches but does not provide feedback and ability to receive missing
documents. This would be similar to https://sign.diia.gov.ua/ but maybe with additional information about application process.

Another considered option is to make system fully inside the Telegram but that limits flexibility with
generating and signing documents and would generally limit user interface and comfort of use.

Then, it could be system without saving applicants' documents that just re-sends them
to Application Committee's email or something like this. This is way more secure solution (or rather
it relieves responsibility from system and transfers it back to the Application Committee).
Main downside here is that problem with feedback and missing documents remains unsolved.

## Cross-cutting concerns

The security and privacy issues are not prioritized because of following considerations:

* The system will have limited number of users (about 1000-1500 yearly) and will be active only few weeks per year;
* Developing of complex security system would require unreasonably large amount of time and effort.

Although, basic security issues must be addressed with proper choice of host, database, etc and modern
programming best-practices.

In the future, in case of successful testing, system must be secured more.
