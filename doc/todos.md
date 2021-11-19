
## improvements
- instead of hardcoding NYC, Boston, get a list of the 100 biggest cities in the US
- make the program realize that NYC and New York City are the same thing
- Go might be a tough one with lots of false positives! Just match case-sensitive?
- Also take a look at the ads == single job posts during the month
    - (though that requires that I go through ever post again!)

- also add the functionality to filter by company
- fix the encoding issues
  maybe relevant: https://en.wikipedia.org/wiki/Percent-encoding

- make the entire thing asynchronous.
- automate the table & table schema creation
- put the entire thing in a docker container
- with all the Seq[String]s I have in my model, doing a NoSQL db might actually be better!
- add metrics with http4s prometheus middleware
- ADD TESTS
## Todos

[x] exchange ujson (which is super nice but uses mutability)
with circe (which is not as nice, but immutable)

[x] at the moment i just assume that all fields in the response exist
that is probably not a good move. fix that

[ ] slick supports codegen of the db entity class
use that

[ ] let the data flow through a stream that converts all of the synonyms into one name before
committing to the db

