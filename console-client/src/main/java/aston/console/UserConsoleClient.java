package aston.console;

import aston.dto.CreateUserRequestDto;
import aston.dto.UpdateUserRequestDto;
import aston.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Scanner;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Slf4j
@Component
public class UserConsoleClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    public UserConsoleClient(RestTemplate restTemplate, @Value("${app.user-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void run() {

        while (running) {
            showMenu();
            int choice = readChoice();
            executeChoice(choice);
        }
        scanner.close();
    }

    private int readChoice() {
        while (true) {
            try {
                System.out.println("Ваш выбор:");
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice < 1 || choice > 7) {
                    System.out.println("Введите число от 1 до 7");
                    log.error("Введено некорректное число");
                } else {
                    return choice;
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Введите целое число");
                log.error("Введен неверный символ");
            }
        }
    }

    private void showMenu() {
        System.out.println("\n---Menu---");
        System.out.println("1. Создать нового пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Найти пользователя по email");
        System.out.println("4. Показать всех пользователей");
        System.out.println("5. Обновить данные пользователя");
        System.out.println("6. Удалить пользователя");
        System.out.println("7. Выход");
    }

    private void executeChoice(int choice) {
        try {
            switch (choice) {
                case 1 -> createNewUser();
                case 2 -> findUserById();
                case 3 -> findUserByEmail();
                case 4 -> findAllUsers();
                case 5 -> updateUser();
                case 6 -> deleteUser();
                case 7 -> exitApp();
                default -> System.out.println("Неизвестный пункт меню");
            }
        } catch (RuntimeException e) {
            handleUserFriendlyError(e);
        }
    }

    private void createNewUser() {
        System.out.println("--Создание пользователя--");
        log.info("Начинаем создание нового пользователя");
        System.out.println("Введите имя:");
        String name = scanner.nextLine().trim();
        System.out.println("Введите email:");
        String email = scanner.nextLine().trim();
        System.out.println("Введите возраст:");

        try {
            int age = Integer.parseInt(scanner.nextLine());
            CreateUserRequestDto requestDto = new CreateUserRequestDto(name, email, age);

            ResponseEntity<User> response = restTemplate.postForEntity(baseUrl, requestDto, User.class);
            User user = response.getBody();
            System.out.println("Новый пользователь с ID = " + user.getId() + " успешно создан");
            log.info("Пользователь успешно создан: ID={}", user.getId());
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат возраста. Должен быть числом.");
            log.error("Ошибка преобразования возраста в число", e);
        } catch (RuntimeException e) {
            handleUserFriendlyError(e);
            log.error("Ошибка создания пользователя: {}", e.getMessage());
        }
    }

    private void findUserById() {
        System.out.println("--Поиск пользователя по ID--");
        log.info("Начинаем поиск пользователя по ID");
        System.out.println("Введите ID:");

        try {
            Long id = Long.parseLong(scanner.nextLine());
            ResponseEntity<User> response = restTemplate.getForEntity(
                    baseUrl + "/" + id, User.class);

            if (response.hasBody()) {
                User user = response.getBody();
                System.out.println("Пользователь найден: " + user);
                log.info("Найден пользователь с ID: {}", id);
            } else {
                System.out.println("Пользователь с ID " + id + " не найден");
                log.warn("Пользователь с ID {} не найден", id);
            }
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("Пользователь с указанным ID не найден");
            log.warn("Пользователь с ID не найден (404)");
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат ID. Должен быть числом.");
            log.error("Ошибка преобразования ID в число", e);
        } catch (RuntimeException e) {
            handleUserFriendlyError(e);
        }
    }

    private void findUserByEmail() {
        System.out.println("--Поиск пользователя по Email--");
        log.info("Начинаем поиск пользователя по Email");
        System.out.println("Введите Email:");
        String email = scanner.nextLine().trim();
        try {
            ResponseEntity<User> response = restTemplate.getForEntity(
                    baseUrl + "/email/" + email, User.class);

            if (response.hasBody()) {
                User user = response.getBody();
                System.out.println("Пользователь найден: " + user);
                log.info("Найден пользователь с email: {}", email);
            } else {
                System.out.println("Пользователь с Email " + email + " не найден");
                log.warn("Пользователь с email {} не найден", email);
            }
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("Пользователь с Email " + email + " не найден");
            log.warn("Пользователь с email {} не найден (404)", email);
        } catch (RuntimeException e) {
            handleUserFriendlyError(e);
        }

    }

    private void findAllUsers() {
        System.out.println("--Вывод всех пользователей--");
        log.info("Начинаем вывод всех пользователей");

        try {
            ResponseEntity<List<User>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<User>>() {});

            List<User> users = response.getBody();

            System.out.println("Список пользователей:");
            if (users.isEmpty()) {
                System.out.println("пуст");
                log.info("Пустой список пользователей");
            } else {
                users.forEach(System.out::println);
                log.info("Выведено {} пользователей", users.size());
            }
        } catch (RuntimeException e) {
            handleUserFriendlyError(e);
        }
    }

    private void updateUser() {
        System.out.println("--Обновление пользователя--");
        log.info("Начинаем обновление пользователя");
        System.out.println("Введите ID:");

        try {
            Long id = Long.parseLong(scanner.nextLine());

            System.out.println("Введите имя. Оставьте пустым, чтобы оставить без изменений");
            String name = scanner.nextLine().trim();
            UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
            if (!name.isEmpty()) {
                requestDto.setName(name);
            }
            System.out.println("Введите email. Оставьте пустым, чтобы оставить без изменений");
            String email = scanner.nextLine().trim();
            if (!email.isEmpty()) {
                if (email.contains("@")) {
                    requestDto.setEmail(email);
                } else {
                    System.out.println("Некорректный формат email. Поле оставлено без изменений.");
                }
            }
            System.out.println("Введите возраст. Оставьте пустым, чтобы оставить без изменений");
            String ageInput = scanner.nextLine().trim();
            if (!ageInput.isEmpty()) {
                try {
                    requestDto.setAge(Integer.parseInt(ageInput));
                } catch (NumberFormatException e) {
                    System.out.println("Некорректный формат возраста. Должен быть числом.");
                    log.error("Ошибка преобразования возраста в число", e);
                    return;
                }
            }

            if (requestDto.isNameSet() || requestDto.isEmailSet() || requestDto.isAgeSet()) {
                try {
                    restTemplate.put(baseUrl + "/" + id, requestDto);
                    System.out.println("Пользователь с ID = " + id + " успешно обновлен");
                    log.info("Пользователь с ID {} успешно обновлен", id);
                } catch (HttpClientErrorException.NotFound e) {
                    System.out.println("Пользователь с ID " + id + " не найден");
                    log.warn("Попытка обновления несуществующего пользователя с ID {}", id);
                } catch (RuntimeException e) {
                    handleUserFriendlyError(e);
                }
            } else {
                System.out.println("Нет данных для обновления. Операция отменена.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат ID. Должен быть числом.");
            log.error("Ошибка преобразования ID в число", e);
        }
    }

    private void deleteUser() {
        System.out.println("--Удаление пользователя по ID--");
        log.info("Начинаем удаление пользователя по ID");
        Long id = getValidUserIdFromInput();
        if (id == null) {
            return;
        }
        try {
            restTemplate.delete(baseUrl + "/" + id);
            System.out.println("Пользователь с ID = " + id + " успешно удален");
            log.info("Пользователь с ID {} успешно удален", id);
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("Пользователь с ID " + id + " не найден для удаления");
            log.warn("Попытка удаления несуществующего пользователя с ID {}", id);
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат ID. Должен быть числом.");
            log.error("Ошибка преобразования ID в число", e);
        } catch (RuntimeException e) {
            handleUserFriendlyError(e);
        }
    }
    private Long getValidUserIdFromInput() {
        System.out.println("Введите ID:");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("ID не может быть пустым.");
            log.warn("Попытка удаления с пустым ID");
            return null;
        }

        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат ID. Должен быть числом.");
            log.error("Ошибка преобразования ID в число: {}", input, e);
            return null;
        }
    }

    private void exitApp() {
        running = false;
        log.info("Выбран пункт меню выход");
        System.out.println("До свидания!");
    }


    public void close() throws Exception {
        scanner.close();
        log.info("Консольный интерфейс ConsoleUI закрыт.");
    }

    private static void handleUserFriendlyError(RuntimeException e) {
        String message;

        if (e instanceof HttpClientErrorException) {
            HttpClientErrorException httpError = (HttpClientErrorException) e;
            switch (httpError.getStatusCode()) {
                case BAD_REQUEST ->
                        message = "Ошибка: неверный запрос. Проверьте корректность введённых данных.";
                case NOT_FOUND ->
                        message = "Ошибка: ресурс не найден (404). Проверьте ID или другие параметры.";
                case CONFLICT ->
                        message = "Ошибка: конфликт данных. Возможно, email уже используется.";
                case UNPROCESSABLE_ENTITY ->
                        message = "Ошибка: невозможно обработать запрос. Проверьте формат данных.";
                default ->
                        message = "HTTP ошибка: " + httpError.getStatusCode() + ". " +
                                "Подробности: " + e.getMessage();
            }
        } else if (e instanceof HttpServerErrorException) {
            message = "Ошибка сервера (5xx). Сервис временно недоступен.";
        } else if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
            message = "Ошибка: не удаётся подключиться к серверу. Проверьте, запущен ли user-service на порту 8085.";
        } else {
            message = "Произошла непредвиденная ошибка: " + e.getMessage() +
                    ". Подробности записаны в лог.";
            log.error("Unhandled exception in user interface", e);
        }

        System.out.println("\n!!!" + message + "!!!\n");
    }
}