# E-Mail Notificator
E-mail notificator based on a SQL result

## Usage Example

**settings.properties**
```
sedmail.host=mail.example.com
sedmail.username=user@example.com
sedmail.password=1234

from=John Smith <john.smith@example.com>

subject=Newsletter!

db.connectionString=jdbc:mysql://localhost:3306/test?useSSL=false
db.username=root
db.password=1234
```

**recepients.sql**
```
-- E-mail must be the first column.
SELECT DISTINCT email, id, CONCAT(firstname, ' ', lastname) as name 
    FROM customers
    WHERE registered = true
    ORDER BY id
```

**message.txt**
```
Dear ${NAME},
As usual, we send you our newsletter for the current month:
....
....
The whole version on http://www.example.com/newsletter/user/${ID}

Sincerely yours
...
```
All the variables must be in upper case.

Run it:
```
java -jar target/email-notificator-1.0.jar settings.properties recepients.sql message.txt
```