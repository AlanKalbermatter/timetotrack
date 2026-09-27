import {
    addDays,
    formatClock,
    formatDuration,
    fromInputValue,
    localDateKey,
    secondsBetween,
    startOfWeek,
    toInputValue,
} from "./time";

describe("formatDuration", () => {
    it("formats minutes under an hour", () => expect(formatDuration(45 * 60)).toBe("45m"));
    it("formats hours with zero-padded minutes", () => expect(formatDuration(3600 + 5 * 60)).toBe("1h 05m"));
    it("never goes negative", () => expect(formatDuration(-10)).toBe("0m"));
});

describe("formatClock", () => {
    it("formats h:mm:ss", () => expect(formatClock(3725)).toBe("1:02:05"));
});

describe("secondsBetween", () => {
    it("measures closed entries", () =>
        expect(secondsBetween("2026-03-10T09:00:00Z", "2026-03-10T10:30:00Z")).toBe(5400));
    it("measures running entries against now", () =>
        expect(secondsBetween("2026-03-10T09:00:00Z", null, new Date("2026-03-10T09:00:42Z"))).toBe(42));
});

describe("startOfWeek", () => {
    it("returns Monday at local midnight", () => {
        const monday = startOfWeek(new Date(2026, 2, 12, 15, 30)); // Thursday 12 March 2026
        expect(monday.getDay()).toBe(1);
        expect(monday.getHours()).toBe(0);
        expect(localDateKey(monday)).toBe("2026-03-09");
    });
    it("treats Sunday as the last day of the week", () =>
        expect(localDateKey(startOfWeek(new Date(2026, 2, 15, 10)))).toBe("2026-03-09"));
});

describe("addDays", () => {
    it("crosses month boundaries", () => expect(localDateKey(addDays(new Date(2026, 0, 31), 1))).toBe("2026-02-01"));
});

describe("datetime-local conversion", () => {
    it("round-trips through the input format in local time", () => {
        const date = new Date(2026, 2, 10, 9, 30);
        expect(toInputValue(date)).toBe("2026-03-10T09:30");
        expect(fromInputValue("2026-03-10T09:30")).toBe(date.toISOString());
    });
});
