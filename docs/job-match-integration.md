# Job-to-Employee Match Percentage Integration

This document explains how the employee-to-job compatibility score is calculated and how other services should collaborate with `job-service` to surface the percentage on the employee job details view.

## Overview

`job-service` exposes `GET /api/jobs/{jobId}/details` which now accepts an optional `employeeId` query parameter. When the parameter is supplied, `job-service`:

1. Loads the persisted job posting (including skills, location, experience bounds, and work mode).
2. Calls `employee-service` to retrieve a lightweight profile for the requesting employee.
3. Runs the data through `JobMatchService` to compute a weighted compatibility percentage and criterion-level breakdown.
4. Returns the enhanced `JobDetailsResponse` payload with the `matchBreakdown` section populated.

If `employeeId` is omitted or the employee profile cannot be fetched (for example, because the Authorization header is missing or invalid), the response simply omits the breakdown.

## Endpoint Contract

```
GET /api/jobs/{jobId}/details?employeeId={employeeId}
Authorization: Bearer <JWT from authentication-service>
```

### Response excerpt

```json
{
  "id": 42,
  "title": "Senior Backend Engineer",
  "employer": { ... },
  "matchBreakdown": {
    "overallMatchPercentage": 75.0,
    "skillsMatchPercentage": 50.0,
    "locationMatchPercentage": 100.0,
    "experienceMatchPercentage": 100.0,
    "skillsWeightPercentage": 50.0,
    "locationWeightPercentage": 20.0,
    "experienceWeightPercentage": 30.0,
    "matchedSkills": ["Python"],
    "missingSkills": ["Cloud Computing"],
    "summary": "Overall match 75.0% (skills 50.0%, location 100.0%, experience 100.0%)"
  }
}
```

Clients should treat the entire `matchBreakdown` block as optional.

## Data Retrieval Requirements

* **Authentication** – For employee-specific calls, forward the user's JWT from the gateway/API layer to `job-service` through the `Authorization` header. `job-service` reuses it when contacting `employee-service`.
* **Employee data** – `job-service` requests `GET /api/employees/{employeeId}` from `employee-service`. Ensure that endpoint remains accessible to other internal services with the forwarded JWT. The returned payload must include:
  * `skills` (list of strings)
  * `location` (string)
  * `experienceYears` (integer)
* **Job data** – Persist required skills in `Job.keySkills`, location in `Job.location`, work mode in `Job.workMode`, and experience bounds in `Job.minExperienceYears`/`Job.maxExperienceYears` when employers create or edit jobs.

## Matching Algorithm

`JobMatchService` weights the three criteria as follows:

| Criterion            | Weight | Calculation summary |
|----------------------|--------|---------------------|
| Skills               | 50%    | Ratio of employee skills that match the job's required skills (case-insensitive). Missing required skills are surfaced for UI hints. |
| Location             | 20%    | Exact city/country match scores 100%. Partial matches (e.g., same state) receive 60% (on-site) or 70% (hybrid). Remote roles always receive 100%. When either side lacks data, we default to 50%. |
| Work experience      | 30%    | Employees meeting the required range receive 100%. Under-qualified or over-qualified candidates receive a proportional score relative to the nearest bound. |

The overall match percentage is the weighted sum of the normalized criterion scores. All percentages are rounded to one decimal place.

### Example

Employee payload:

```
Skills: Python, Java, Project Management
Location: Seattle
Experience: 5 years (software development)
```

Job posting data:

```
Required Skills: Python, Cloud Computing
Location: Remote
Minimum Experience: 3 years (software development)
```

Resulting breakdown:

* Skills – 1 of 2 required skills (Python) found ⇒ 50% contribution ⇒ 25% of total score.
* Location – Remote job ⇒ full credit ⇒ 20% of total score.
* Experience – Employee exceeds minimum ⇒ full credit ⇒ 30% of total score.

**Overall match: 25% + 20% + 30% = 75%.**

## Front-end Integration Tips

* Always call the `/details` endpoint with both the job id and the signed-in employee id to obtain the compatibility score.
* Use `matchBreakdown.summary` for quick display or render individual percentages for richer visualizations.
* Highlight `missingSkills` to help employees decide whether to upskill before applying.

## Extensibility Notes

* The weights are centralized inside `JobMatchService`; adjust them there if product requirements change.
* Additional criteria (e.g., salary expectations, preferred industries) can be appended by extending `JobMatchBreakdown` and updating `JobMatchService`.
* To add caching between services, wrap `EmployeeProfileClient.fetchEmployee` with a cache store such as Redis.
