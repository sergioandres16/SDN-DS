package com.example.dataservice.Controller;
/*
import com.example.dataservice.Dtos.CreateUserRequest;
import com.example.dataservice.Repository.CursoRepository;
import com.example.dataservice.Repository.GruposInvestigacionRepository;
import com.example.dataservice.Repository.SectoresAdministracionRepository;
*/
import com.example.dataservice.Repository.UserRepository;
import com.example.dataservice.Entity.*;
import com.example.dataservice.Entity.Credential;
import com.example.dataservice.Entity.User;
import com.example.dataservice.Utils.JavaUtils;
import org.json.HTTP;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
@RequestMapping(value = "/user")
@CrossOrigin
public class OfficialDataService implements InitializingBean {

    private final UserRepository userRepository;

    public OfficialDataService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
    }

    @PostMapping(value = "")
    public ResponseEntity<String> handlePostRequest(@RequestBody String requestBody) {
        // Puedes procesar la solicitud POST aquí según tus necesidades.
        // En este ejemplo, simplemente se devuelve una respuesta con un código de estado 200 (OK).
        return ResponseEntity.ok("Solicitud POST exitosa");
    }
    @PostMapping(value = "/is-authenticated", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity isAuthenticatedByUsername(@RequestBody(required = false) Credential credential) {

        HashMap<String, Object> responseMap = new HashMap<>();
        HashMap<String, Object> responseHeader = new HashMap<>();
        HashMap<String, Object> responseBody = new HashMap<>();
        HttpStatus httpStatus = null;
        if (credential.getUsername() == null || credential.getPassword() == null) {
            responseHeader.put("message", "Invalid Request");
            responseBody.put("isAuthenticated", false);
            responseMap.put("header", responseHeader);

            httpStatus = HttpStatus.BAD_REQUEST;

        } else {
            Optional<User> userOpt = userRepository.findByPasswordAndUsername(credential.getPassword(), credential.getUsername());

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                responseHeader.put("message", "User Authenticated");
                responseBody.put("isAuthenticated", true);
                //responseBody.put("roles", assiggnRole(user));
                responseMap.put("header", responseHeader);
                responseMap.put("body", responseBody);
            } else {
                responseHeader.put("message", "Invalid User");
                responseBody.put("isAuthenticated", false);
                responseMap.put("header", responseHeader);
                responseMap.put("body", responseBody);
            }
            httpStatus = HttpStatus.OK;

        }

        return new ResponseEntity(responseMap, httpStatus);
    }
    /*
    public List<String> assiggnRole(User user) {

        List<String> roles = new ArrayList<>();

        if (user.getCode() != null && user.getCode().equals("SecretAdmin")) {
            roles.add("Network Admin");
        }
        if (user.getCursosDictados() != null) {
            roles.add("Profesor");
        }
        if (user.getCursosMatriculados() != null) {
            System.out.println("Es Estudiante");
            roles.add("Estudiante");
        }
        if (user.getAreaGestion() != null) {
            roles.add("Administrativo");
        }
        if (user.getGrupoInvestigacion() != null) {
            roles.add("Investigador");
        }

        return roles;
    }*/
/*
    @Autowired
    private MappingMongoConverter mappingMongoConverter;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CursoRepository cursoRepository;

    @Autowired
    SectoresAdministracionRepository sectoresAdministracionRepository;

    @Autowired
    GruposInvestigacionRepository gruposInvestigacionRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
    }

    @PostMapping(value = "/is-authenticated", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity isAuthenticatedByUsername(@RequestBody(required = false) Credential credential) {
        // falta validar no enviar ningun request

        HashMap<String, Object> responseMap = new HashMap<>();
        HashMap<String, Object> responseHeader = new HashMap<>();
        HashMap<String, Object> responseBody = new HashMap<>();
        HttpStatus httpStatus = null;
        if (credential.getUsername() == null || credential.getPassword() == null) {
            responseHeader.put("message", "Invalid Request");
            responseBody.put("isAuthenticated", false);
            responseMap.put("header", responseHeader);

            httpStatus = HttpStatus.BAD_REQUEST;

        } else {
            Optional<User> userOpt = userRepository.findByContraseniaAndCodigo(credential.getPassword(), credential.getUsername());

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                responseHeader.put("message", "User Authenticated");
                responseBody.put("isAuthenticated", true);
                responseBody.put("roles", assiggnRole(user));
                responseMap.put("header", responseHeader);
                responseMap.put("body", responseBody);
            } else {
                responseHeader.put("message", "Invalid User");
                responseBody.put("isAuthenticated", false);
                responseMap.put("header", responseHeader);
                responseMap.put("body", responseBody);
            }
            httpStatus = HttpStatus.OK;

        }

        return new ResponseEntity(responseMap, httpStatus);
    }

    @GetMapping(value = "/list-all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllUsers() {
        HashMap<String, Object> responseMap = new HashMap<>();
        HashMap<String, Object> responseHeader = new HashMap<>();
        HashMap<String, Object> responseBody = new HashMap<>();
        HttpStatus httpStatus = null;

        // TODO not include null values
        List<User> users = userRepository.findByCode(null);

        responseHeader.put("message", "Ok");
        responseBody.put("users", users);
        responseMap.put("header", responseHeader);
        responseMap.put("body", responseBody);

        return new ResponseEntity(users, HttpStatus.OK);
    }


    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createUser(@RequestBody @Valid CreateUserRequest newUser, BindingResult bindingResult) {

        boolean userValid = false;
        boolean valUniqueCode = false;
        boolean valStudent = false;
        boolean valProfesor = false;
        boolean valInvestigator = false;
        boolean valAdministrative = false;

        HashMap<String, Object> responseMap = new HashMap<>();
        HashMap<String, Object> responseHeader = new HashMap<>();
        HashMap<String, Object> responseBody = new HashMap<>();
        HttpStatus httpStatus = null;

        // validaciones generales - campos obligatorios
        if (bindingResult.hasErrors()) {
            System.out.println("Somethins is wrnong");
        }

        // validacion de la relacion rol seleccionado con rol asignado
        // como estamos creando un usuario, no existe informacion de este en la BD
        // por lo que se plantea esta validacion sin el metodo assignRole
        // este solo sirve para usuarios creados
        // TODO if more roles were created its necessary to  change this
        /*
        for (String rol : newUser.getRols()) {

            if (rol.equalsIgnoreCase("Estudiante") && ((newUser.getEspecialidad() != null || newUser.getEspecialidad().trim().equals("")) &&
                    (newUser.getFacultad() != null || newUser.getFacultad().trim().equals("")) &&
                    (newUser.getCursosMatriculados() != null))) {
                valStudent = true;
            }else if (rol.equalsIgnoreCase("Profesor") && ((newUser.getEspecialidad() != null || newUser.getEspecialidad().trim().equals("")) &&
                    (newUser.getFacultad() != null || newUser.getFacultad().trim().equals("")) &&
                    (newUser.getCursosDictados() != null))) {
                valProfesor = true;
            }else if(rol.equalsIgnoreCase("Investigador") && newUser.getGrupoInvestigacion() != null){
                valInvestigator = true;
            }else if (rol.equalsIgnoreCase("Administrativo") && newUser.getAreaGestion() != null) {
                valAdministrative = true;
            }

        } */
    /*
        System.out.println(valProfesor);
        System.out.println(valStudent);
        System.out.println(valAdministrative);
        System.out.println(valInvestigator);
        // validar código único
        // must be only one to validate
        System.out.println(newUser.getCodigo());
        Optional<User> userOpt = userRepository.findByCodigo(newUser.getCodigo());

        if(!userOpt.isPresent()){
            valUniqueCode = true;
        }else {
            // TODO set message in header
            System.out.println("Must be unique");
        }

        // trabajar únicamente para 4 roles de manera que se puedan aumentar más

        userValid = valUniqueCode; //TODO
        if (userValid) {
            User userDB = new User(newUser.getId(), newUser.getNombres(), newUser.getApellidos(), newUser.getFacultad(), newUser.getEspecialidad(), newUser.getCursosMatriculados(), newUser.getCursosDictados(), newUser.getAreaGestion(), newUser.getCodigo(), hashSha256(newUser.getContrasenia()), newUser.getGrupoInvestigacion(), newUser.getCode());
            // los nulos no se guardan
            userRepository.save(userDB);
            responseBody.put("codigo", userDB.getCodigo());
            responseHeader.put("message", "User "+ userDB.getNombres() + " saved successfully");
            responseMap.put("header", responseHeader);
            responseMap.put("body", responseBody);
            httpStatus = HttpStatus.CREATED;

        } else {
            System.out.println("User not valid");
        }

        return new ResponseEntity(responseMap, httpStatus);
        */
    }
/*
    @PutMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editUser(@RequestBody User user) {

        HashMap<String, Object> responseMap = new HashMap<>();
        HashMap<String, Object> responseHeader = new HashMap<>();
        HashMap<String, Object> responseBody = new HashMap<>();
        HttpStatus httpStatus = null;
        if(user.getCodigo() != null){
            Optional<User> userOpt = userRepository.findByCodigo(user.getCodigo());

            if(userOpt.isPresent()){
                User userDB = userOpt.get();

                if(user.getNombres() != null && !userDB.getNombres().equals(user.getNombres())){
                    userDB.setNombres(user.getNombres());
                }
                if(user.getApellidos() != null && !userDB.getApellidos().equals(user.getApellidos())){
                    userDB.setApellidos(user.getApellidos());
                }
                if(user.getCode() != null && !userDB.getCode().equals(user.getCode())){
                    userDB.setCode(user.getCode());
                }
                if(userDB.getFacultad() == null  || (user.getFacultad() != null && !userDB.getFacultad().equals(user.getFacultad()))){
                    userDB.setFacultad(user.getFacultad());
                }
                if(userDB.getEspecialidad() == null || (user.getEspecialidad() != null && !userDB.getEspecialidad().equals(user.getEspecialidad()))){
                    userDB.setEspecialidad(user.getCode());
                }
                if(userDB.getCursosMatriculados() == null || (user.getCursosMatriculados() != null && !userDB.getCursosMatriculados().equals(user.getCursosMatriculados()))){
                    userDB.setCursosMatriculados(user.getCursosMatriculados());
                }
                if(userDB.getCursosDictados() == null || (user.getCursosDictados() != null && !userDB.getCursosDictados().equals(user.getCursosDictados()))){
                    userDB.setCursosDictados(user.getCursosDictados());
                }
                if(userDB.getAreaGestion() == null || (user.getAreaGestion() != null && !userDB.getAreaGestion().equals(user.getAreaGestion()))){
                    userDB.setAreaGestion(user.getAreaGestion());
                }
                if(user.getCodigo() != null && !userDB.getCodigo().equals(user.getCodigo())){
                    userDB.setCodigo(user.getCodigo());
                }
                if(user.getContrasenia() != null && !userDB.getContrasenia().equals(user.getContrasenia())){
                    userDB.setContrasenia(hashSha256(user.getContrasenia()));
                }
                if(userDB.getGrupoInvestigacion() == null || (user.getGrupoInvestigacion() != null && !userDB.getGrupoInvestigacion().equals(user.getGrupoInvestigacion()))){
                    userDB.setGrupoInvestigacion(user.getGrupoInvestigacion());
                }
                userRepository.save(userDB);
                responseHeader.put("message", "User " + userDB.getNombres() + " was succesfully edited");
                responseMap.put("header", responseHeader);
                responseBody.put("userId", userDB.getCodigo());
                responseMap.put("body", responseBody);
                httpStatus = HttpStatus.OK;
            }else {
                responseHeader.put("message", "Invalid User Code");
                responseMap.put("header", responseHeader);
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        }else{
            responseHeader.put("message", "Invalid User Code");
            responseMap.put("header", responseHeader);
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(responseMap, httpStatus);
    }

    @GetMapping(value = "/get-user")
    public ResponseEntity getUserByCodigo(@RequestParam(value = "codigo", required = false) String codigo){
        HashMap<String, Object> responseMap = new HashMap<>();
        HashMap<String, Object> responseHeader = new HashMap<>();
        HashMap<String, Object> responseBody = new HashMap<>();
        HttpStatus httpStatus = null;
        ResponseEntity responseEntity = null;
        if(codigo != null){
            Optional<User> userOpt = userRepository.findByCodigo(codigo);
            if(userOpt.isPresent()){
                User userDB = userOpt.get();
                responseHeader.put("message","OK");
                responseMap.put("body", userDB);
                responseMap.put("header", responseHeader);
                httpStatus = HttpStatus.OK;
                responseEntity = new ResponseEntity(userDB, httpStatus);
            }else{
                responseHeader.put("message", "Invalid User Code");
                responseMap.put("header", responseHeader);
                httpStatus = HttpStatus.BAD_REQUEST;
                responseEntity = new ResponseEntity(responseMap, httpStatus);
            }
        }else{
            responseHeader.put("message", "Invalid User Code");
            responseMap.put("header", responseHeader);
            httpStatus = HttpStatus.BAD_REQUEST;
            responseEntity = new ResponseEntity(responseMap, httpStatus);

        }

        return responseEntity;
    }

    @DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteUserByStudentCode(@RequestBody String request) {

        HashMap<String, Object> responseMap = new HashMap<>();
        HashMap<String, Object> responseHeader = new HashMap<>();
        HashMap<String, Object> responseBody = new HashMap<>();
        HttpStatus httpStatus = null;

        Map<String, Object> requestMap = JavaUtils.jsonToMap(request);
        String codigo = (String) requestMap.get("codigo");

        if(codigo != null){
            Optional<User> userOpt = userRepository.findByCodigo(codigo);

            if(userOpt.isPresent()){
                User userDB = userOpt.get();
                System.out.println(userDB.getCodigo());
                userRepository.deleteById(userDB.getId());

                responseBody.put("userId", userDB.getCode());
                responseHeader.put("message", "User "+ userDB.getNombres()+" deleted succesfully");
                responseMap.put("header", responseHeader);
                httpStatus = HttpStatus.OK;

            }else{
                responseHeader.put("message", "Invalid User Code");
                responseMap.put("header", responseHeader);
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        }else {
            responseHeader.put("message", "Invalid User Code");
            responseMap.put("header", responseHeader);
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity(responseMap, httpStatus);
    }

    @GetMapping(value = "/course/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity listMatriculatedCourses(@RequestParam(value = "action", required = false) String action){
        HashMap<String, Object> responseMap = new HashMap<>();
        HashMap<String, Object> responseHeader = new HashMap<>();
        HashMap<String, Object> responseBody = new HashMap<>();
        HttpStatus httpStatus = null;
        List<Curso> cursos = null;
        ResponseEntity responseEntity = null;

        if(action.equals("dictados")){
            // list of courses in general - to the profesor
            cursos = cursoRepository.findAll();
            responseBody.put("body", cursos);
            responseHeader.put("message", "OK");
            responseMap.put("header", responseHeader);
            httpStatus = HttpStatus.OK;
            responseEntity = new ResponseEntity(cursos, httpStatus);
        }else if (action.equals("matriculados")){
            // list of courses actives - for students
            cursos = cursoRepository.findByStatus("Activo");
            responseBody.put("body", cursos);
            responseHeader.put("message", "OK");
            responseMap.put("header", responseHeader);
            httpStatus = HttpStatus.OK;
            responseEntity = new ResponseEntity(cursos, httpStatus);
        }else {
            // TODO - find the way to
            responseHeader.put("message", "Invalid Action");
            responseMap.put("header", responseHeader);
            httpStatus = HttpStatus.BAD_REQUEST;
            responseEntity = new ResponseEntity(responseMap, httpStatus);

        }

        return responseEntity;
    }

    @GetMapping(value = "/researcher/groups", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity listResearcherGroups(){
        // TODO - validation

        HashMap<String, Object> responseMap = new HashMap<>();
        HashMap<String, Object> responseHeader = new HashMap<>();
        HashMap<String, Object> responseBody = new HashMap<>();

        List<GrupoInvestigacion> gruposInvestigacion = gruposInvestigacionRepository.findAll();

        return new ResponseEntity(gruposInvestigacion, HttpStatus.OK);
    }

    @GetMapping(value = "/administrative/areas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity listAdministrativeAreas(){
        // TODO - validation
        HashMap<String, Object> responseMap = new HashMap<>();
        HashMap<String, Object> responseHeader = new HashMap<>();
        HashMap<String, Object> responseBody = new HashMap<>();

        List<SectoresAdministracion> sectoresAdministracions = sectoresAdministracionRepository.findAll();
        return new ResponseEntity(sectoresAdministracions, HttpStatus.OK);
    }
    public List<String> assiggnRole(User user) {

        List<String> roles = new ArrayList<>();

        if (user.getCode() != null && user.getCode().equals("SecretAdmin")) {
            roles.add("Network Admin");
        }
        if (user.getCursosDictados() != null) {
            roles.add("Profesor");
        }
        if (user.getCursosMatriculados() != null) {
            System.out.println("Es Estudiante");
            roles.add("Estudiante");
        }
        if (user.getAreaGestion() != null) {
            roles.add("Administrativo");
        }
        if (user.getGrupoInvestigacion() != null) {
            roles.add("Investigador");
        }

        return roles;
    }

    public String hashSha256(String password) {
        String hashedPassword = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            hashedPassword = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPassword;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleException(HttpServletRequest request) {
        HashMap<String, Object> responseMap = new HashMap<>();

        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT") || request.getMethod().equals("GET")) {
            responseMap.put("estado", "error");
            responseMap.put("message", "Debe enviar un valor");
        }
        return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
    }
}*/
