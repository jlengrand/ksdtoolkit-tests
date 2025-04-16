package nl.lengrand

import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.integration.EulerIntegration
import hr.unipu.ksdtoolkit.simulations.Simulation


fun main(){
    println("elgherhkg")

    val test = RandomTest()
}


class RandomTest() {

    // Static properties (optional)
    companion object {
        const val TOTAL_POPULATION_VALUE = 10000            // [customer]
        const val ADVERTISING_EFFECTIVENESS_VALUE = 0.011   // [1/year]
        const val CONTACT_RATE_VALUE = 100                  // [1/year]
        const val ADOPTION_FRACTION_VALUE = 0.015           // []

        const val INITIAL_TIME_VALUE = 0    // [year]
        const val FINAL_TIME_VALUE = 10     // [year]
        const val TIME_STEP_VALUE = 0.25    // [year]
    }

    init {
        println("init")

        val model = Model()

        // Override default model properties
        model.initialTime = INITIAL_TIME_VALUE
        model.finalTime = FINAL_TIME_VALUE
        model.timeStep = TIME_STEP_VALUE
        model.integrationType = EulerIntegration()
        model.name = "Innovation/Product Diffusion Model" // optional

        val TOTAL_POPULATION = model.constant("TOTAL_POPULATION")
        val ADVERTISING_EFFECTIVENESS = model.constant("ADVERTISING_EFFECTIVENESS")
        val CONTACT_RATE = model.constant("CONTACT_RATE")
        val ADOPTION_FRACTION = model.constant("ADOPTION_FRACTION")

        val adoptionFromAdvertising =
            model.converter("adoptionFromAdvertising")
        val adoptionFromWordOfMouth =
            model.converter("adoptionFromWordOfMouth")

        val Potential_Adopters = model.stock("Potential_Adopters")
        val Adopters = model.stock("Adopters")

        val adoptionRate = model.flow("adoptionRate")


        Potential_Adopters.initialValue = { TOTAL_POPULATION }
        Adopters.initialValue = { 0.0 }
        TOTAL_POPULATION.equation = { TOTAL_POPULATION_VALUE }
        ADVERTISING_EFFECTIVENESS.equation = { ADVERTISING_EFFECTIVENESS_VALUE }
        CONTACT_RATE.equation = { CONTACT_RATE_VALUE }
        ADOPTION_FRACTION.equation = { ADOPTION_FRACTION_VALUE }


        adoptionFromAdvertising.equation =
            { Potential_Adopters * ADVERTISING_EFFECTIVENESS }
        adoptionFromWordOfMouth.equation =
            { Potential_Adopters * (CONTACT_RATE * ADOPTION_FRACTION * (Adopters / TOTAL_POPULATION) ) }

        Potential_Adopters.equation = { - adoptionRate }
        Adopters.equation = { adoptionRate }

        adoptionRate.equation =
            { adoptionFromAdvertising + adoptionFromWordOfMouth }

        val simulation = Simulation(model)
        simulation.outputs {
//            CsvExporter("output.csv", ";")    // Text
//            PngExporter("chart.png")           // Image
            WinSimulator()                      // Desktop
        }

        simulation.run()
    }


}