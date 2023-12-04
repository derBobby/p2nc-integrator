# Pretix2Nextcloud integrator
This applications main purpose is to handle incoming WebHooks of a Pretix ticket shop.
Currently the project hosts additional classes required to access the Pretix API.

## Features
* Handle incoming WebHooks:
  * Create Nextcloud account (event "pretix.event.order.placed.require_approval")
  * Notify if order needs approval (event "pretix.event.order.approved")
* Filter for incoming WebHooks based on organization, event, questions and answers.
* Notifications: Email, Signal

## Status

[![Merge Dependabot PR](https://github.com/derBobby/p2nc-integrator/actions/workflows/dependabot-automerge.yml/badge.svg)](https://github.com/derBobby/p2nc-integrator/actions/workflows/dependabot-automerge.yml)

[![CD](https://github.com/derBobby/p2nc-integrator/actions/workflows/dependabot-automerge.yml/badge.svg)](https://github.com/derBobby/p2nc-integrator/actions/workflows/dependabot-automerge.yml)