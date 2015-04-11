<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Appdirect Hiring Test</title>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">

    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>
        <h1>User Attributes</h1>
        <table class="table table-border">
            <thead>
            <tr>
                <th>Attribute Name</th>
                <th>Attribute Value</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>ID</td>
                <td>${auth.identityUrl}</td>
            </tr>
            <#list auth.attributes as attribute
            <tr>
                <td>${attribute.name}</td>
                <td>
                    <dl th:each="value : ${attribute.values}">
                    <#list attribute.values as value>
                        <dd>${value}</dd>
                    </#list>
                    </dl>
                </td>
            </tr>
            </#list>
            </tbody>
        </table><br/>
          <a href="/logout">Logout</a>
  </body>
</html>