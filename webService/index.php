<?php
  require_once 'connection.php';

  $response = array();

  if(isset($_GET['apicall'])) {
    switch($_GET['apicall']) {
      case 'signup':
        if(isTheseParametersAvailable(array('username','email','password','gender'))) {
          $username = $_POST['username'];
          $email = $_POST['email'];
          $password = md5($_POST['password']);
          $gender = $_POST['gender'];

          $stmt = $conn -> prepare("SELECT id_user FROM users WHERE username = ? OR email = ?");

          $stmt -> bind_param("ss", $username, $email);
          $stmt -> execute();
          $stmt -> store_result();

          if($stmt -> num_rows > 0) {
            $response['error'] = true;
            $response['message'] = 'Ya fue registrado este usuario';
            $stmt -> close();
          } else {
            $stmt = $conn -> prepare("INSERT INTO users (username, email, password, gender) VALUES (?, ?, ?, ?)");
            $stmt -> bind_param("ssss", $username, $email, $password, $gender);

            if($stmt -> execute()) {
              $stmt = $conn -> prepare("SELECT id_user, username, email, gender FROM users WHERE username = ?");
              $stmt -> bind_param("s", $username);
              $stmt -> execute();
              $stmt -> bind_result($id, $username, $email, $gender);
              $stmt -> fetch();

              $user = array(
                'id' => $id,
                'username' => $username,
                'email' => $email,
                'gender' => $gender
              );

              $stmt -> close();

              $response['error'] = false;
              $response['message'] = 'Usuario registrado correctamente';
              $response['user'] = $user;
            }
          }
        } else {
          $response['error'] = true;
          $response['message'] = 'Se necesitan los datos para continuar';
        }

        break;

    case 'login':
      if(isTheseParametersAvailable(array('username', 'password'))) {
        $username = $_POST['username'];
        $password = md5($_POST['password']);

        $stmt = $conn -> prepare("SELECT id_user, username, email, gender FROM users WHERE username = ? AND password = ?");
        $stmt -> bind_param("ss", $username, $password);
        $stmt -> execute();
        $stmt -> store_result();

        if($stmt -> num_rows > 0) {
          $stmt -> bind_result($id, $username, $email, $gender);
          $stmt -> fetch();
          $user = array(
            'id' => $id,
            'username' => $username,
            'email' => $email,
            'gender' => $gender
          );

          $response['error'] = false;
          $response['message'] = 'Bienvenido';
          $response['user'] = $user;
        } else {
          $response['error'] = false;
          $response['message'] = 'Usuario o contraseña inválidos';
        }
      }
      break;

    default:
      $response['error'] = true;
      $response['message'] = 'Operación inválida';
      break;
    }
  } else {
    $response['error'] = true;
    $response['message'] = 'Llamada a la API incorrecta';
  }

  echo json_encode($response);

  function isTheseParametersAvailable($params) {
    foreach($params as $param)
      if(!isset($_POST[$param])) return false;

    return true;
  }
?>