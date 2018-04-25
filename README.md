# E-Mail Notificator
E-mail notificator based on a SQL result

## Usage Example

**sendmail.properties**
```
host = mail.example.com
username = user@example.com
password = 123
from.name=John Smith
from.email=john.smith@example.com
subject=Newsletter!
```

**recepients.sql**
```
-- E-mail must be the first column.
SELECT email, id, CONCAT(firstname, ' ', lastname) as name 
    FROM customers
    WHERE registered = true
    GROUP BY email
```

**message.txt**
```
Dear ${name},
As usual, we send you our newsletter for the current month:
....
....
The whole version on http://www.example.com/newsletter/user/${id}

Sincerely yours
...
```

Run it:
```
java -jar target/email-notificator-1.0.jar sendmail.properties recepients.sql message.txt
```