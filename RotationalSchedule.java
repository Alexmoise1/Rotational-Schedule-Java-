import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RotationalSchedule {
    enum Crew { RED_CREW, BLUE_CREW }
    enum Status { WORKING, OFF_DUTY }

    private static final int CYCLE_LENGTH = 14;
    private static final LocalDate ROTATION_START_DATE =
            LocalDate.of(LocalDate.now().getYear(), 1, 1);
    private static final Crew STARTING_CREW = Crew.BLUE_CREW;

    public class ScheduleEntry {
        private Crew crew;
        private Status status;
        private LocalDate date;
        public ScheduleEntry(Crew crew, Status status, LocalDate date) {
            this.crew = crew;
            this.status = status;
            this.date = date;
        }
        public Crew getCrew() { return crew; }
        public Status getStatus() { return status; }
        public LocalDate getDate() { return date; }
    }

    public Map<Crew, Status> getCrewStatus(LocalDate queryDate) {
        Map<Crew, Status> crewStatus = new HashMap<>();
        long daysSinceStart = ChronoUnit.DAYS.between(ROTATION_START_DATE, queryDate);
        long blockIndex = Math.floorDiv(daysSinceStart, CYCLE_LENGTH);
        boolean startingCrewWorking = (blockIndex % 2 == 0);
        Crew working = startingCrewWorking ? STARTING_CREW
                : (STARTING_CREW == Crew.RED_CREW ? Crew.BLUE_CREW : Crew.RED_CREW);
        Crew off = (working == Crew.RED_CREW) ? Crew.BLUE_CREW : Crew.RED_CREW;
        crewStatus.put(working, Status.WORKING);
        crewStatus.put(off, Status.OFF_DUTY);
        return crewStatus;
    }

    public List<ScheduleEntry> getScheduleRange(LocalDate startDate, LocalDate endDate) {
        List<ScheduleEntry> schedule = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            Map<Crew, Status> dailyStatus = getCrewStatus(currentDate);
            schedule.add(new ScheduleEntry(Crew.RED_CREW, dailyStatus.get(Crew.RED_CREW), currentDate));
            schedule.add(new ScheduleEntry(Crew.BLUE_CREW, dailyStatus.get(Crew.BLUE_CREW), currentDate));
            currentDate = currentDate.plusDays(1);
        }
        return schedule;
    }

    public LocalDate getNextCrewChange(LocalDate fromDate) {
        long daysSinceStart = ChronoUnit.DAYS.between(ROTATION_START_DATE, fromDate);
        int dayInBlock = Math.floorMod((int) daysSinceStart, CYCLE_LENGTH);
        int daysUntilBoundary = CYCLE_LENGTH - dayInBlock;
        if (daysUntilBoundary == 0) daysUntilBoundary = CYCLE_LENGTH;
        return fromDate.plusDays(daysUntilBoundary);
    }

    private void printRangeTable(LocalDate start, LocalDate end) {
        List<ScheduleEntry> schedule = getScheduleRange(start, end);
        System.out.println("Date         \tRed Crew  \tBlue Crew");
        System.out.println("------------------------------------------------");
        for (int i = 0; i < schedule.size(); i += 2) {
            ScheduleEntry red  = schedule.get(i);
            ScheduleEntry blue = schedule.get(i + 1);
            System.out.printf("%-12s\t%-9s\t%-9s%n",
                    red.getDate(), red.getStatus(), blue.getStatus());
        }
    }

    public void displayYearToDateAndRest(LocalDate today) {
        LocalDate jan1  = LocalDate.of(today.getYear(), 1, 1);
        LocalDate dec31 = LocalDate.of(today.getYear(), 12, 31);
        System.out.println("=== Rotational Schedule (" + today.getYear() + ") ===");
        System.out.println("Anchor (first " + STARTING_CREW + " day): " + ROTATION_START_DATE);
        System.out.println();
        System.out.println("[A] Year-to-date ( " + jan1 + "  →  " + today + " )");
        printRangeTable(jan1, today);
        System.out.println();
        if (today.isBefore(dec31)) {
            System.out.println("[B] Rest of the year ( " + today.plusDays(1) + "  →  " + dec31 + " )");
            printRangeTable(today.plusDays(1), dec31);
            System.out.println();
        } else {
            System.out.println("[B] Rest of the year: (none — today is Dec 31)");
            System.out.println();
        }
        System.out.println("Next crew change after today: " + getNextCrewChange(today));
    }

    public static void main(String[] args) {
        RotationalSchedule scheduler = new RotationalSchedule();
        LocalDate today = (args.length >= 1) ? LocalDate.parse(args[0]) : LocalDate.now();
        scheduler.displayYearToDateAndRest(today);
    }
}
