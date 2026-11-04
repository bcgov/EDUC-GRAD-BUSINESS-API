[![img](https://img.shields.io/badge/Lifecycle-Stable-97ca00)](https://github.com/bcgov/repomountie/blob/master/doc/lifecycle-badges.md) &nbsp;
[![Build](https://github.com/bcgov/EDUC-GRAD-BUSINESS-API/actions/workflows/on.pr.yml/badge.svg)](https://github.com/bcgov/EDUC-GRAD-BUSINESS-API/actions/workflows/on.pr.yml) &nbsp;
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=bcgov_EDUC-GRAD-BUSINESS-API&metric=bugs)](https://sonarcloud.io/summary/new_code?id=bcgov_EDUC-GRAD-BUSINESS-API) &nbsp;
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=bcgov_EDUC-GRAD-BUSINESS-API&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=bcgov_EDUC-GRAD-BUSINESS-API) &nbsp;
![Sonar Tests](https://img.shields.io/sonar/tests/bcgov_EDUC-GRAD-BUSINESS-API?compact_message&server=https%3A%2F%2Fsonarcloud.io) &nbsp;
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=bcgov_EDUC-GRAD-BUSINESS-API&metric=coverage)](https://sonarcloud.io/summary/new_code?id=bcgov_EDUC-GRAD-BUSINESS-API) &nbsp;
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=bcgov_EDUC-GRAD-BUSINESS-API&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=bcgov_EDUC-GRAD-BUSINESS-API) &nbsp;
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=bcgov_EDUC-GRAD-BUSINESS-API&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=bcgov_EDUC-GRAD-BUSINESS-API) &nbsp;
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=bcgov_EDUC-GRAD-BUSINESS-API&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=bcgov_EDUC-GRAD-BUSINESS-API) &nbsp;
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=bcgov_EDUC-GRAD-BUSINESS-API&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=bcgov_EDUC-GRAD-BUSINESS-API) &nbsp;
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=bcgov_EDUC-GRAD-BUSINESS-API&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=bcgov_EDUC-GRAD-BUSINESS-API) &nbsp;
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=bcgov_EDUC-GRAD-BUSINESS-API&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=bcgov_EDUC-GRAD-BUSINESS-API) &nbsp;
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=bcgov_EDUC-GRAD-BUSINESS-API&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=bcgov_EDUC-GRAD-BUSINESS-API) &nbsp;
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=bcgov_EDUC-GRAD-BUSINESS-API&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=bcgov_EDUC-GRAD-BUSINESS-API) &nbsp;

# EDUC-GRAD-BUSINESS-API 
GRAD Business API for external clients

## PDF retention

The business API removes regular PDF files directly under `/tmp` whose last
modification time is more than 24 hours old. Cleanup runs daily at 03:00 in
`America/Vancouver`; files can therefore remain for almost 48 hours. It does not
traverse subdirectories or follow symbolic links. The filename year is not used.
The first scheduled run after deployment also removes existing expired PDFs.

Configure cleanup through these environment variables:

| Variable | Default | Purpose |
| --- | --- | --- |
| `PDF_CLEANUP_ENABLED` | `true` | Set to `false` to disable cleanup. |
| `PDF_CLEANUP_DIRECTORY` | `/tmp` | Directory containing generated PDFs. |
| `PDF_CLEANUP_RETENTION_DAYS` | `1` | Retention in 24-hour days; must be at least 1. |
| `PDF_CLEANUP_CRON` | `0 0 3 * * *` | Spring cron expression (including seconds). |
| `PDF_CLEANUP_ZONE` | `America/Vancouver` | Schedule time zone. |

Each replica runs cleanup against the shared PVC. Deletion is idempotent and
missing files are tolerated, so no distributed scheduler lock is required.
Cleanup logs the number of files deleted and their total size in bytes; individual
deletion failures are logged and do not prevent processing the remaining files.

![GitHub repo size](https://img.shields.io/github/repo-size/bcgov/EDUC-GRAD-BUSINESS-API) &nbsp;
![GitHub Tag](https://img.shields.io/github/v/tag/bcgov/EDUC-GRAD-BUSINESS-API) &nbsp;
![GitHub commits difference between two branches/tags/commits](https://img.shields.io/github/commits-difference/bcgov/EDUC-GRAD-BUSINESS-API?base=main&head=grad-release&label=grad-release%20-%3E%20main) &nbsp;
![GitHub commits difference between two branches/tags/commits](https://img.shields.io/github/commits-difference/bcgov/EDUC-GRAD-BUSINESS-API?base=grad-release&head=main&label=main%20-%3E%20grad-release) &nbsp;
