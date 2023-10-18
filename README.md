# PretixToNextcloudIntegrator
This applications main purpose is to handle incoming WebHooks of a Pretix ticket shop.
Currently the project hosts additional classes required to access the Pretix API.

## Features
* Handle incoming WebHooks:
  * Create Nextcloud account (event "pretix.event.order.placed.require_approval")
  * Notify if order needs approval (event "pretix.event.order.approved")
* Filter for incoming WebHooks based on organization, event, questions and answers.
* Notifications: Email, Signal

## Status
![Deployment](https://github.com/derBobby/PretixToNextcloudIntegrator/actions/workflows/cd.yml/badge.svg)
![CodeQL](https://github.com/derBobby/PretixToNextcloudIntegrator/actions/workflows/codeql.yml/badge.svg)
![Test](https://github.com/derBobby/PretixToNextcloudIntegrator/actions/workflows/test.yml/badge.svg)
