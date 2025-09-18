# Rotational-Schedule-Java-
A Java program that generates a 14-day rotational work schedule for two crews (Red and Blue). It shows which crew is working or off duty, prints the year-to-date and rest of the year schedules, and reports the next crew change.

Usage

Compile:

javac -d bin src/RotationalSchedule.java


Run with today’s date:

java -cp bin RotationalSchedule


Run with a custom date:

java -cp bin RotationalSchedule 2025-02-15

Example
=== Rotational Schedule (2025) ===
[A] Year-to-date (2025-01-01 → 2025-02-15)
Date         Red Crew   Blue Crew
2025-01-01   OFF_DUTY   WORKING
...

[B] Rest of the year (2025-02-16 → 2025-12-31)
...

Next crew change after today: 2025-02-26
