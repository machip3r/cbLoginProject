<?php
    $servername = "localhost";
    $username = "id17839179_maki";
    $password = "f7fhU+THn/>hifCd";
    $database = "id17839179_webservice";
    $conn = new mysqli($servername, $username, $password, $database);

    if ($conn -> connect_error) die("Fallo de conexión: " . $conn -> connect_error);
?>
