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
    <style>
    body { padding-top: 55px; }
    p { padding: 15px; }
    .bg-warning { font-weight: bold; }
    </style>
  </head>
  <body>
  <nav class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
          <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
              <span class="sr-only">Toggle navigation</span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">AppDirect Integration</a>
          </div>
          <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
              <li><a href="#">Home</a></li>
              <li><a href="/profile">Profile</a></li>
              <li><a href="/users">Users</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
  </nav>

      <div class="container">
          <p class="bg-warning">
            You need to be logged to proceed
          </p>
          <form action="/login/openid" method="POST">
            <div class="form-group">
            <label for="openid">OpenID</label>
            <input id="openid" class="form-control" name="openid_identifier" value="${openId!""}" placeholder="https://www.example.com/openid/id/ec5d8eda-5cec-444d-9e30-125b6e4b67e2"/><br/>
            <button type="submit" class="btn btn-default">Log in with AppDirect</button>
          </form>
      </div>
  </body>
</html>