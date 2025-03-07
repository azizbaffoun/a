package tn.esprit.pidev.services;

import com.google.gson.annotations.SerializedName;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;
import tn.esprit.pidev.config.TodoistConfig;
import tn.esprit.pidev.models.Maintenance;
import tn.esprit.pidev.models.Materiel;
import tn.esprit.pidev.models.Emprunt;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoistService {
    private static final String API_BASE_URL = "https://api.todoist.com/rest/v2/";
    private final TodoistApi api;
    private final Map<String, String> projectIds;

    public TodoistService() {
        String apiToken = TodoistConfig.getApiToken();
        if (apiToken == null || apiToken.isEmpty()) {
            throw new IllegalStateException("Todoist API token not configured");
        }

        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> chain.proceed(
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + apiToken)
                    .build()
            ))
            .build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        this.api = retrofit.create(TodoistApi.class);
        this.projectIds = new HashMap<>();
        initializeProjects();
    }

    private void initializeProjects() {
        try {
            // Create or get projects for different management aspects
            String materialProject = createOrGetProject("Material Management");
            String maintenanceProject = createOrGetProject("Maintenance");
            String loanProject = createOrGetProject("Loan Management");

            projectIds.put("material", materialProject);
            projectIds.put("maintenance", maintenanceProject);
            projectIds.put("loan", loanProject);
        } catch (IOException e) {
            System.err.println("Failed to initialize Todoist projects: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String createOrGetProject(String name) throws IOException {
        Response<List<Project>> projectsResponse = api.getProjects().execute();
        if (!projectsResponse.isSuccessful()) {
            System.err.println("Failed to get projects. Response code: " + projectsResponse.code());
            System.err.println("Response body: " + projectsResponse.errorBody().string());
            throw new IOException("Failed to get projects");
        }

        if (projectsResponse.body() != null) {
            for (Project project : projectsResponse.body()) {
                if (project.name.equals(name)) {
                    return project.id;
                }
            }
        }

        Response<Project> newProjectResponse = api.createProject(new Project(name)).execute();
        if (!newProjectResponse.isSuccessful()) {
            System.err.println("Failed to create project. Response code: " + newProjectResponse.code());
            System.err.println("Response body: " + newProjectResponse.errorBody().string());
            throw new IOException("Failed to create project");
        }

        if (newProjectResponse.body() != null) {
            return newProjectResponse.body().id;
        }
        throw new IOException("Failed to create project: No response body");
    }

    public void createMaintenanceTask(Maintenance maintenance, Materiel materiel) {
        try {
            String dueDate = maintenance.getDateMaintenance().format(DateTimeFormatter.ISO_DATE);
            String content = String.format("Maintenance for %s (ID: %d)", 
                materiel.getType(), 
                materiel.getId());
            String description = String.format("Description: %s\nStatus: %s", 
                maintenance.getDescription(),
                maintenance.getStatutMaintenance().toString());

            Task task = new Task(
                content,
                description,
                dueDate,
                projectIds.get("maintenance")
            );

            Response<Task> response = api.createTask(task).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Failed to create maintenance task");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createLoanTask(Emprunt emprunt, Materiel materiel) {
        try {
            String dueDate = emprunt.getDateRetour().format(DateTimeFormatter.ISO_DATE);
            String content = String.format("Loan Return: %s (ID: %d)", 
                materiel.getType(), 
                materiel.getId());
            String description = String.format("Loan Date: %s\nReturn Date: %s\nStatus: %s", 
                emprunt.getDateEmprunt().format(DateTimeFormatter.ISO_DATE),
                emprunt.getDateRetour().format(DateTimeFormatter.ISO_DATE),
                emprunt.getStatutEmprunt().toString());

            Task task = new Task(
                content,
                description,
                dueDate,
                projectIds.get("loan")
            );

            Response<Task> response = api.createTask(task).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Failed to create loan task");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateMaintenanceTaskStatus(Maintenance maintenance, boolean completed) {
        try {
            Response<List<Task>> tasksResponse = api.getTasks().execute();
            if (tasksResponse.isSuccessful() && tasksResponse.body() != null) {
                String searchPattern = String.format("Maintenance for .* (ID: %d)", maintenance.getMaterielID());
                for (Task task : tasksResponse.body()) {
                    if (task.content.matches(searchPattern)) {
                        if (completed) {
                            api.closeTask(task.id).execute();
                        } else {
                            api.reopenTask(task.id).execute();
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLoanTaskStatus(Emprunt emprunt, boolean completed) {
        try {
            Response<List<Task>> tasksResponse = api.getTasks().execute();
            if (tasksResponse.isSuccessful() && tasksResponse.body() != null) {
                String searchPattern = String.format("Loan Return: .* (ID: %d)", emprunt.getMaterielID());
                for (Task task : tasksResponse.body()) {
                    if (task.content.matches(searchPattern)) {
                        if (completed) {
                            api.closeTask(task.id).execute();
                        } else {
                            api.reopenTask(task.id).execute();
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private interface TodoistApi {
        @GET("projects")
        Call<List<Project>> getProjects();

        @POST("projects")
        Call<Project> createProject(@Body Project project);

        @GET("tasks")
        Call<List<Task>> getTasks();

        @POST("tasks")
        Call<Task> createTask(@Body Task task);

        @POST("tasks/{id}/close")
        Call<Void> closeTask(@Path("id") String id);

        @POST("tasks/{id}/reopen")
        Call<Void> reopenTask(@Path("id") String id);
    }

    private static class Project {
        @SerializedName("id")
        String id;

        @SerializedName("name")
        String name;

        Project(String name) {
            this.name = name;
        }
    }

    private static class Task {
        @SerializedName("id")
        String id;

        @SerializedName("content")
        String content;

        @SerializedName("description")
        String description;

        @SerializedName("due_date")
        String dueDate;

        @SerializedName("project_id")
        String projectId;

        Task(String content, String description, String dueDate, String projectId) {
            this.content = content;
            this.description = description;
            this.dueDate = dueDate;
            this.projectId = projectId;
        }
    }
} 