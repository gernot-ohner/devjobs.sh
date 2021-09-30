# Idea

provide a nice interface to go through the posts from the hn jobs post

## Pseudocode

1. get the jobs posts
2. parse them 
3. identify their children
4. get all of those
5. parse them, extracting
    ... company 
    ... locations (words that match, e.g. NYC, Boston, remote)
    ... technologies (words that match, e.g. Java, Go, Scala )
    ... levels (words matching junior, mid-level, senior)
    ... salary (words matching $... or ...USD)
    ... remote-friendliness (matching "remote", but not "not remote" or "currently remote")
6. store them in a DB (postgres in docker?)
7. expose a (functional TM) HTTP interface that allows you to provide 


## improvements
- instead of hardcoding NYC, Boston, get a list of the 100 biggest cities in the US
- make the program realize that NYC and New York City are the same thing
- Go might be a tough one with lots of false positives! Just match case-sensitive?
- Also take a look at the ads == single job posts during the month
  - (though that requires that I go through ever post again!)
 
- also add the functionality to filter by company

- make the entire thing asynchronous.
- automate the table & table schema creation
- put the entire thing in a docker container
- with all the Seq[String]s I have in my model, doing a NoSQL db might actually be better!
## Todos

[x] exchange ujson (which is super nice but uses mutability)
with circe (which is not as nice, but immutable)

[x] at the moment i just assume that all fields in the response exist
that is probably not a good move. fix that

[ ] slick supports codegen of the db entity class
    use that

[ ] let the data flow through a stream that converts all of the synonyms into one name before
   committing to the db

