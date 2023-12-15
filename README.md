# Pretix2Nextcloud integrator
This Spring Boot application creates Nextcloud accounts for incoming Webhooks from a Pretix ticket shop.

## Features
* Filter for incoming WebHooks based on organization, event, questions and answers.
* Handle incoming WebHooks:
  * Notify via Signal messenger and Email, if order needs approval
  * Create Nextcloud account

## Status

[![Merge Dependabot PR](https://github.com/derBobby/p2nc-integrator/actions/workflows/dependabot-automerge.yml/badge.svg)](https://github.com/derBobby/p2nc-integrator/actions/workflows/dependabot-automerge.yml)

[![CD](https://github.com/derBobby/p2nc-integrator/actions/workflows/test-and-deploy.yml/badge.svg)](https://github.com/derBobby/p2nc-integrator/actions/workflows/test-and-deploy.yml)