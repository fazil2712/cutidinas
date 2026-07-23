package com.ppip.cutidinas.service;

import com.ppip.cutidinas.model.Holiday;
import com.ppip.cutidinas.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private static final String ID_HOLIDAY_URL = "https://calendar.google.com/calendar/ical/id.indonesian%23holiday%40group.v.calendar.google.com/public/basic.ics";

    public int syncIndonesianHolidays() {
        int addedCount = 0;
        try {
            URL url = new URL(ID_HOLIDAY_URL);
            try (InputStream is = url.openStream()) {
                CalendarBuilder builder = new CalendarBuilder();
                Calendar calendar = builder.build(is);

                for (Object component : calendar.getComponents()) {
                    if (component instanceof VEvent) {
                        VEvent event = (VEvent) component;
                        Property dtStart = event.getProperty(Property.DTSTART);
                        Property summary = event.getProperty(Property.SUMMARY);

                        if (dtStart != null && summary != null) {
                            String dateStr = dtStart.getValue();
                            if (dateStr != null && dateStr.length() >= 8) {
                                try {
                                    int year = Integer.parseInt(dateStr.substring(0, 4));
                                    int month = Integer.parseInt(dateStr.substring(4, 6));
                                    int day = Integer.parseInt(dateStr.substring(6, 8));
                                    LocalDate localDate = LocalDate.of(year, month, day);

                                    String summaryText = summary.getValue();
                                    
                                    // Exclude joint holidays (Cuti Bersama)
                                    if (summaryText != null && !summaryText.toLowerCase().contains("cuti bersama")) {
                                        if (holidayRepository.findByDate(localDate).isEmpty()) {
                                            Holiday holiday = new Holiday();
                                            holiday.setDate(localDate);
                                            holiday.setDescription(summaryText);
                                            holidayRepository.save(holiday);
                                            addedCount++;
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("Failed to parse event date: " + dateStr);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to sync holidays from Google Calendar: " + e.getMessage());
        }
        return addedCount;
    }
}
