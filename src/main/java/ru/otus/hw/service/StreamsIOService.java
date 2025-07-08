package ru.otus.hw.service;

import ru.otus.hw.exceptions.AnswerReadException;

import java.io.*;

public class StreamsIOService implements IOService {
    private final PrintStream printStream;
    private final BufferedReader reader;

    public StreamsIOService(PrintStream printStream, InputStream inputStream) {

        this.printStream = printStream;
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public void printLine(String s) {
        printStream.println(s);
    }

    @Override
    public void printFormattedLine(String s, Object... args) {
        printStream.printf(s + "%n", args);
    }

    @Override
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new AnswerReadException("Problem reading from stream", e);
        }
    }
}
