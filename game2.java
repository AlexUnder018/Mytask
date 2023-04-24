package com.example.fxproject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class game2 extends Application {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private static final int RADIUS = 100;
    private static final int MAX_DISTANCE = 10;
    private static final double MAX_SPEED = 0.5;
    private static final Color PERFECT_COLOR = Color.LIGHTGREEN;
    private static final Color BAD_COLOR = Color.RED;

    private Canvas canvas;
    private Label infoLabel;
    private Label Label;
    private int centerX;
    private int centerY;
    private boolean isDrawing;
    private long startTime;
    private int numPoints;
    private int numBadPoints;
    private int numAttempts;

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);

        infoLabel = new Label();
        infoLabel.setText("Attempt 1: Draw a perfect circle!");
        Label = new Label("In each try please wait several seconds to see result");
        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setBottom(infoLabel);
        root.setTop(Label);
        root.setStyle("-fx-border-color: transparent"); // Remove border

        Scene scene = new Scene(root, Color.WHITE); // Set background color to white
        primaryStage.setScene(scene);
        primaryStage.show();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        centerX = WIDTH / 2;
        centerY = HEIGHT / 2;
        gc.strokeOval(centerX - RADIUS, centerY - RADIUS, RADIUS * 2, RADIUS * 2);
    }

    private void handleMousePressed(MouseEvent event) {
        canvas.getGraphicsContext2D().clearRect(0, 0, WIDTH, HEIGHT); // clear canvas
        isDrawing = true;
        startTime = System.nanoTime();
        numPoints = 0;
        numBadPoints = 0;
        numAttempts++;

        infoLabel.setText(String.format("Attempt %d: Draw a perfect circle!", numAttempts));
    }

    private double lastX;
    private double lastY;
    private void handleMouseDragged(MouseEvent event) {
        if (isDrawing) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setLineWidth(5); // increase the width of the line to 5 pixels
            gc.strokeLine(lastX, lastY, event.getX(), event.getY());
            lastX = event.getX();
            lastY = event.getY();
            gc.setFill(Color.TRANSPARENT);
            gc.fillOval(event.getX(), event.getY(), 2, 2);

            numPoints++;
            double distance = getDistance(event.getX(), event.getY(), centerX, centerY);
            if (distance < RADIUS - MAX_DISTANCE || distance > RADIUS + MAX_DISTANCE) {
                numBadPoints++;
            }

            double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
            double speed = numPoints / elapsedTime;
            if (speed < MAX_SPEED) {
                numBadPoints++;
            }

            if (numPoints == 360) {
                double accuracy = (1 - (double) numBadPoints / numPoints) * 100;
                if (accuracy < 0) {
                    accuracy = 0;
                }
                if (accuracy > 100) {
                    accuracy = 100;
                }
                infoLabel.setText(String.format("Attempt %d: Accuracy: %.1f%%", numAttempts, accuracy));
                if (accuracy == 100) {
                    gc.setStroke(PERFECT_COLOR);
                } else {
                    double colorFactor = accuracy / 100.0;
                    gc.setStroke(Color.color(
                            BAD_COLOR.getRed() + colorFactor * (PERFECT_COLOR.getRed() - BAD_COLOR.getRed()),
                            BAD_COLOR.getGreen() + colorFactor * (PERFECT_COLOR.getGreen() - BAD_COLOR.getGreen()),
                            PERFECT_COLOR.getGreen() * (1 - colorFactor) + BAD_COLOR.getGreen() * colorFactor,
                            BAD_COLOR.getBlue() * (1 - colorFactor) + PERFECT_COLOR.getBlue() * colorFactor
                    ));
                }
                gc.strokeOval(centerX - RADIUS, centerY - RADIUS, RADIUS * 2, RADIUS * 2);
                isDrawing = false;
            } else {
                double accuracy = (1 - (double) numBadPoints / numPoints) * 100;
                if (accuracy < 0) {
                    accuracy = 0;
                }
                if (accuracy > 100) {
                    accuracy = 100;
                }
                double colorFactor = accuracy / 100.0;
                gc.setStroke(Color.color(
                        BAD_COLOR.getRed() + colorFactor * (PERFECT_COLOR.getRed() - BAD_COLOR.getRed()),
                        BAD_COLOR.getGreen() + colorFactor * (PERFECT_COLOR.getGreen() - BAD_COLOR.getGreen()),
                        PERFECT_COLOR.getGreen() * (1 - colorFactor) + BAD_COLOR.getGreen() * colorFactor,
                        BAD_COLOR.getBlue() * (1 - colorFactor) + PERFECT_COLOR.getBlue() * colorFactor
                ));
                gc.strokeLine(event.getX(), event.getY(), event.getX() + 1, event.getY() + 1);
            }
        }
    }




    private void handleMouseReleased(MouseEvent event) {
            if (isDrawing) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setStroke(BAD_COLOR);
                gc.strokeOval(centerX - RADIUS, centerY - RADIUS, RADIUS * 2, RADIUS * 2);
                isDrawing = false;
            }
        }

        private double getDistance(double x1, double y1, double x2, double y2) {
            double dx = x1 - x2;
            double dy = y1 - y2;
            return Math.sqrt(dx * dx + dy * dy);
        }

        public static void main(String[] args) {
            launch(args);
        }
    }

