package app.coronawarn.datadonation.common.validation;

import java.time.LocalDate;

public class ObjectWithDateMember {

  /**
   * from = "1970-01-01", till = "2000-01-01".
   */
  @DateInRange(from = "1970-01-01", till = "2000-01-01")
  LocalDate dateToBeValidated;

  /**
   * from = "", till = "".
   */
  @DateInRange
  LocalDate dateToBeValidatedNull;

  /**
   * from = "1970-01-01", till = "".
   */
  @DateInRange(from = "1970-01-01", message = "Date must be after {from}")
  LocalDate dateToBeValidatedFrom;

  /**
   * from = "", till = "2000-01-01".
   */
  @DateInRange(till = "2000-01-01", message = "Date must be before {till}")
  LocalDate dateToBeValidatedTill;

  public void setDateToBeValidated(final LocalDate dateToBeValidated) {
    this.dateToBeValidated = dateToBeValidated;
  }

  public void setDateToBeValidatedFrom(final LocalDate dateToBeValidatedFrom) {
    this.dateToBeValidatedFrom = dateToBeValidatedFrom;
  }

  public void setDateToBeValidatedNull(final LocalDate dateToBeValidatedNull) {
    this.dateToBeValidatedNull = dateToBeValidatedNull;
  }

  public void setDateToBeValidatedTill(final LocalDate dateToBeValidatedTill) {
    this.dateToBeValidatedTill = dateToBeValidatedTill;
  }
}
