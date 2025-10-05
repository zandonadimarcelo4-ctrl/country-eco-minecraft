package com.brazileconomy.mod.command

import com.brazileconomy.mod.BrazilEconomyMod
import com.brazileconomy.mod.economy.*
import com.brazileconomy.mod.util.CPFValidator
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object EconomyCommands {
    
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        registerHelpCommand(dispatcher)
        registerCPFCommands(dispatcher)
        registerBalanceCommands(dispatcher)
        registerPixCommands(dispatcher)
        registerLoanCommands(dispatcher)
        registerInvestmentCommands(dispatcher)
        registerScoreCommands(dispatcher)
        registerStatementCommand(dispatcher)
        registerAdminCommands(dispatcher)
    }
    
    private fun registerHelpCommand(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("ajuda")
                .executes(::showHelp)
        )
    }
    
    private fun registerCPFCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("cpf")
                .then(CommandManager.literal("register")
                    .then(CommandManager.argument("cpf", StringArgumentType.string())
                        .executes(::registerCPF)
                    )
                )
                .then(CommandManager.literal("check")
                    .executes(::checkCPF)
                )
        )
    }
    
    private fun registerBalanceCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("saldo")
                .executes(::checkBalance)
        )
    }
    
    private fun registerPixCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("pix")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                    .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0.01))
                        .executes(::sendPix)
                    )
                )
        )
    }
    
    private fun registerLoanCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("emprestimo")
                .then(CommandManager.literal("pegar")
                    .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(1.0))
                        .executes(::takeLoan)
                    )
                )
                .then(CommandManager.literal("pagar")
                    .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(1.0))
                        .executes(::payLoan)
                    )
                )
                .then(CommandManager.literal("listar")
                    .executes(::listLoans)
                )
        )
    }
    
    private fun registerInvestmentCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("investir")
                .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(1.0))
                    .then(CommandManager.argument("taxaMensal", DoubleArgumentType.doubleArg(0.01))
                        .executes(::makeInvestment)
                    )
                )
        )
        
        dispatcher.register(
            CommandManager.literal("investimentos")
                .then(CommandManager.literal("listar")
                    .executes(::listInvestments)
                )
                .then(CommandManager.literal("sacar")
                    .then(CommandManager.argument("index", IntegerArgumentType.integer(1))
                        .executes(::withdrawInvestment)
                    )
                )
        )
    }
    
    private fun registerScoreCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("score")
                .executes(::checkScore)
        )
    }
    
    private fun registerStatementCommand(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("extrato")
                .executes(::showStatement)
        )
    }
    
    private fun registerAdminCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("ecoadmin")
                .requires { it.hasPermissionLevel(2) }
                .then(CommandManager.literal("add")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0.0))
                            .executes(::addMoney)
                        )
                    )
                )
                .then(CommandManager.literal("remove")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0.0))
                            .executes(::removeMoney)
                        )
                    )
                )
                .then(CommandManager.literal("set")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.argument("amount", DoubleArgumentType.doubleArg(0.0))
                            .executes(::setMoney)
                        )
                    )
                )
                .then(CommandManager.literal("list")
                    .executes(::listAccounts)
                )
                .then(CommandManager.literal("view")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(::viewPlayerAccount)
                    )
                )
        )
    }
    
    private fun showHelp(context: CommandContext<ServerCommandSource>): Int {
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        context.source.sendFeedback({ Text.literal("§a§lBRAZIL ECONOMY - COMANDOS") }, false)
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        context.source.sendFeedback({ Text.literal("§6Financeiro:") }, false)
        context.source.sendFeedback({ Text.literal("§f/saldo §7- Ver seu saldo") }, false)
        context.source.sendFeedback({ Text.literal("§f/pix <jogador> <valor> §7- Enviar dinheiro") }, false)
        context.source.sendFeedback({ Text.literal("§f/extrato §7- Ver histórico de transações") }, false)
        context.source.sendFeedback({ Text.literal("§f/score §7- Ver sua pontuação de crédito") }, false)
        context.source.sendFeedback({ Text.literal("") }, false)
        context.source.sendFeedback({ Text.literal("§6CPF:") }, false)
        context.source.sendFeedback({ Text.literal("§f/cpf check §7- Ver seu CPF") }, false)
        context.source.sendFeedback({ Text.literal("§f/cpf register <cpf> §7- Registrar CPF manualmente") }, false)
        context.source.sendFeedback({ Text.literal("") }, false)
        context.source.sendFeedback({ Text.literal("§6Empréstimos:") }, false)
        context.source.sendFeedback({ Text.literal("§f/emprestimo pegar <valor> §7- Pegar empréstimo") }, false)
        context.source.sendFeedback({ Text.literal("§f/emprestimo pagar <valor> §7- Pagar empréstimo") }, false)
        context.source.sendFeedback({ Text.literal("§f/emprestimo listar §7- Ver seus empréstimos") }, false)
        context.source.sendFeedback({ Text.literal("") }, false)
        context.source.sendFeedback({ Text.literal("§6Investimentos:") }, false)
        context.source.sendFeedback({ Text.literal("§f/investir <valor> <taxa> §7- Fazer investimento") }, false)
        context.source.sendFeedback({ Text.literal("§f/investimentos listar §7- Ver investimentos") }, false)
        context.source.sendFeedback({ Text.literal("§f/investimentos sacar <id> §7- Sacar investimento") }, false)
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        return 1
    }
    
    private fun registerCPF(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.playerOrThrow
        val cpf = StringArgumentType.getString(context, "cpf")
        
        if (!CPFValidator.isValidCPF(cpf)) {
            context.source.sendError(Text.literal("§cCPF inválido! Verifique o número digitado."))
            return 0
        }
        
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(player.uuid, player.name.string)
        
        if (account.hasCpf()) {
            context.source.sendError(Text.literal("§cVocê já possui um CPF registrado: ${account.cpf}"))
            return 0
        }
        
        val formattedCPF = CPFValidator.formatCPF(cpf)
        account.cpf = formattedCPF
        manager.saveData()
        
        context.source.sendFeedback({ Text.literal("§aCPF registrado com sucesso: $formattedCPF") }, false)
        return 1
    }
    
    private fun checkCPF(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.playerOrThrow
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(player.uuid, player.name.string)
        
        if (!account.hasCpf()) {
            context.source.sendError(Text.literal("§cVocê não possui CPF registrado."))
            return 0
        }
        
        context.source.sendFeedback({ Text.literal("§aSeu CPF: ${account.cpf}") }, false)
        return 1
    }
    
    private fun checkBalance(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.playerOrThrow
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(player.uuid, player.name.string)
        
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        context.source.sendFeedback({ Text.literal("§a§lSALDO BANCÁRIO") }, false)
        context.source.sendFeedback({ Text.literal("§fSaldo: §2REI$ ${"%.2f".format(account.balance)}") }, false)
        
        if (account.hasCpf()) {
            context.source.sendFeedback({ Text.literal("§fCPF: §7${account.cpf}") }, false)
        }
        
        val totalLoans = account.loans.sumOf { it.amount }
        if (totalLoans > 0) {
            context.source.sendFeedback({ Text.literal("§fEmpréstimos: §c-REI$ ${"%.2f".format(totalLoans)}") }, false)
        }
        
        val totalInvestments = account.investments.sumOf { it.getCurrentValue() }
        if (totalInvestments > 0) {
            context.source.sendFeedback({ Text.literal("§fInvestimentos: §aREI$ ${"%.2f".format(totalInvestments)}") }, false)
        }
        
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        return 1
    }
    
    private fun sendPix(context: CommandContext<ServerCommandSource>): Int {
        val sender = context.source.playerOrThrow
        val receiver = EntityArgumentType.getPlayer(context, "player")
        val amount = DoubleArgumentType.getDouble(context, "amount")
        
        if (sender.uuid == receiver.uuid) {
            context.source.sendError(Text.literal("§cVocê não pode enviar PIX para si mesmo!"))
            return 0
        }
        
        val manager = BrazilEconomyMod.economyManager
        val senderAccount = manager.getOrCreateAccount(sender.uuid, sender.name.string)
        val receiverAccount = manager.getOrCreateAccount(receiver.uuid, receiver.name.string)
        
        if (!senderAccount.hasCpf()) {
            context.source.sendError(Text.literal("§cVocê precisa ter um CPF registrado!"))
            return 0
        }
        
        if (senderAccount.balance < amount) {
            context.source.sendError(Text.literal("§cSaldo insuficiente! Seu saldo: REI$ ${"%.2f".format(senderAccount.balance)}"))
            return 0
        }
        
        if (manager.transferMoney(sender.uuid, receiver.uuid, amount)) {
            ScoreManager.updateScore(senderAccount)
            ScoreManager.updateScore(receiverAccount)
            manager.saveData()
            
            context.source.sendFeedback({ Text.literal("§a§l✔ PIX ENVIADO COM SUCESSO!") }, false)
            context.source.sendFeedback({ Text.literal("§fValor: §2REI$ ${"%.2f".format(amount)}") }, false)
            context.source.sendFeedback({ Text.literal("§fPara: §e${receiver.name.string}") }, false)
            context.source.sendFeedback({ Text.literal("§fSaldo atual: §2REI$ ${"%.2f".format(senderAccount.balance)}") }, false)
            
            receiver.sendMessage(Text.literal("§a§l✔ PIX RECEBIDO!"))
            receiver.sendMessage(Text.literal("§fValor: §2REI$ ${"%.2f".format(amount)}"))
            receiver.sendMessage(Text.literal("§fDe: §e${sender.name.string}"))
            receiver.sendMessage(Text.literal("§fSaldo atual: §2REI$ ${"%.2f".format(receiverAccount.balance)}"))
            
            return 1
        }
        
        context.source.sendError(Text.literal("§cErro ao processar PIX!"))
        return 0
    }
    
    private fun takeLoan(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.playerOrThrow
        val amount = DoubleArgumentType.getDouble(context, "amount")
        
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(player.uuid, player.name.string)
        
        if (!account.hasCpf()) {
            context.source.sendError(Text.literal("§cVocê precisa ter um CPF registrado!"))
            return 0
        }
        
        if (account.score < 400) {
            context.source.sendError(Text.literal("§cSeu score de crédito é muito baixo para empréstimos!"))
            return 0
        }
        
        val maxLoan = when {
            account.score >= 700 -> 5000.0
            account.score >= 600 -> 2000.0
            account.score >= 500 -> 1000.0
            else -> 500.0
        }
        
        val currentLoans = account.loans.sumOf { it.amount }
        if (currentLoans + amount > maxLoan) {
            context.source.sendError(Text.literal("§cLimite de empréstimo excedido! Seu limite: REI$ ${"%.2f".format(maxLoan - currentLoans)}"))
            return 0
        }
        
        val interestRate = when {
            account.score >= 700 -> 0.15
            account.score >= 600 -> 0.25
            account.score >= 500 -> 0.35
            else -> 0.50
        }
        
        val loan = Loan(amount = amount, interestRate = interestRate)
        account.loans.add(loan)
        account.addBalance(amount)
        
        val transaction = Transaction(
            type = Transaction.TransactionType.LOAN_TAKEN,
            fromPlayer = "BANCO",
            toPlayer = account.playerName,
            amount = amount,
            description = "Empréstimo aprovado (${(interestRate * 100).toInt()}% a.m.)"
        )
        account.addTransaction(transaction)
        
        ScoreManager.updateScore(account)
        manager.saveData()
        
        context.source.sendFeedback({ Text.literal("§a§l✔ EMPRÉSTIMO APROVADO!") }, false)
        context.source.sendFeedback({ Text.literal("§fValor: §2REI$ ${"%.2f".format(amount)}") }, false)
        context.source.sendFeedback({ Text.literal("§fTaxa de juros: §e${(interestRate * 100).toInt()}% ao mês") }, false)
        context.source.sendFeedback({ Text.literal("§fPrazo: §730 dias") }, false)
        context.source.sendFeedback({ Text.literal("§fSaldo atual: §2REI$ ${"%.2f".format(account.balance)}") }, false)
        
        return 1
    }
    
    private fun payLoan(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.playerOrThrow
        val amount = DoubleArgumentType.getDouble(context, "amount")
        
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(player.uuid, player.name.string)
        
        if (account.loans.isEmpty()) {
            context.source.sendError(Text.literal("§cVocê não possui empréstimos ativos!"))
            return 0
        }
        
        val totalDebt = account.loans.sumOf { it.amount }
        if (amount > totalDebt) {
            context.source.sendError(Text.literal("§cValor maior que sua dívida total! Dívida: REI$ ${"%.2f".format(totalDebt)}"))
            return 0
        }
        
        if (account.balance < amount) {
            context.source.sendError(Text.literal("§cSaldo insuficiente! Seu saldo: REI$ ${"%.2f".format(account.balance)}"))
            return 0
        }
        
        account.removeBalance(amount)
        var remaining = amount
        
        account.loans.removeIf { loan ->
            if (remaining >= loan.amount) {
                remaining -= loan.amount
                true
            } else {
                loan.amount -= remaining
                remaining = 0.0
                false
            }
        }
        
        val transaction = Transaction(
            type = Transaction.TransactionType.LOAN_PAID,
            fromPlayer = account.playerName,
            toPlayer = "BANCO",
            amount = amount,
            description = "Pagamento de empréstimo"
        )
        account.addTransaction(transaction)
        
        ScoreManager.updateScore(account)
        manager.saveData()
        
        context.source.sendFeedback({ Text.literal("§a§l✔ PAGAMENTO REALIZADO!") }, false)
        context.source.sendFeedback({ Text.literal("§fValor pago: §2REI$ ${"%.2f".format(amount)}") }, false)
        context.source.sendFeedback({ Text.literal("§fDívida restante: §cREI$ ${"%.2f".format(account.loans.sumOf { it.amount })}") }, false)
        context.source.sendFeedback({ Text.literal("§fSaldo atual: §2REI$ ${"%.2f".format(account.balance)}") }, false)
        
        return 1
    }
    
    private fun listLoans(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.playerOrThrow
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(player.uuid, player.name.string)
        
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        context.source.sendFeedback({ Text.literal("§c§lSEUS EMPRÉSTIMOS") }, false)
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        
        if (account.loans.isEmpty()) {
            context.source.sendFeedback({ Text.literal("§7Você não possui empréstimos ativos.") }, false)
        } else {
            account.loans.forEachIndexed { index, loan ->
                context.source.sendFeedback({ Text.literal("§f${index + 1}. §cREI$ ${"%.2f".format(loan.amount)}") }, false)
                context.source.sendFeedback({ Text.literal("   §7Juros: ${(loan.interestRate * 100).toInt()}% a.m. | Dias restantes: ${loan.getDaysRemaining()}") }, false)
            }
            
            val totalDebt = account.loans.sumOf { it.amount }
            context.source.sendFeedback({ Text.literal("") }, false)
            context.source.sendFeedback({ Text.literal("§fTotal: §c-REI$ ${"%.2f".format(totalDebt)}") }, false)
        }
        
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        return 1
    }
    
    private fun makeInvestment(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.playerOrThrow
        val amount = DoubleArgumentType.getDouble(context, "amount")
        val rate = DoubleArgumentType.getDouble(context, "taxaMensal")
        
        if (rate < 0.01 || rate > 0.50) {
            context.source.sendError(Text.literal("§cTaxa deve estar entre 1% e 50% ao mês!"))
            return 0
        }
        
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(player.uuid, player.name.string)
        
        if (!account.hasCpf()) {
            context.source.sendError(Text.literal("§cVocê precisa ter um CPF registrado!"))
            return 0
        }
        
        if (account.balance < amount) {
            context.source.sendError(Text.literal("§cSaldo insuficiente! Seu saldo: REI$ ${"%.2f".format(account.balance)}"))
            return 0
        }
        
        account.removeBalance(amount)
        val investment = Investment(amount = amount, rate = rate)
        account.investments.add(investment)
        
        val transaction = Transaction(
            type = Transaction.TransactionType.INVESTMENT_MADE,
            fromPlayer = account.playerName,
            toPlayer = "INVESTIMENTOS",
            amount = amount,
            description = "Investimento realizado (${(rate * 100).toInt()}% a.m.)"
        )
        account.addTransaction(transaction)
        
        ScoreManager.updateScore(account)
        manager.saveData()
        
        context.source.sendFeedback({ Text.literal("§a§l✔ INVESTIMENTO REALIZADO!") }, false)
        context.source.sendFeedback({ Text.literal("§fValor: §2REI$ ${"%.2f".format(amount)}") }, false)
        context.source.sendFeedback({ Text.literal("§fRetorno esperado: §e${(rate * 100).toInt()}% ao mês") }, false)
        context.source.sendFeedback({ Text.literal("§fSaldo atual: §2REI$ ${"%.2f".format(account.balance)}") }, false)
        
        return 1
    }
    
    private fun listInvestments(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.playerOrThrow
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(player.uuid, player.name.string)
        
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        context.source.sendFeedback({ Text.literal("§a§lSEUS INVESTIMENTOS") }, false)
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        
        if (account.investments.isEmpty()) {
            context.source.sendFeedback({ Text.literal("§7Você não possui investimentos ativos.") }, false)
        } else {
            account.investments.forEachIndexed { index, inv ->
                val currentValue = inv.getCurrentValue()
                val profit = currentValue - inv.amount
                context.source.sendFeedback({ Text.literal("§f${index + 1}. §aREI$ ${"%.2f".format(currentValue)}") }, false)
                context.source.sendFeedback({ Text.literal("   §7Inicial: REI$ ${"%.2f".format(inv.amount)} | Lucro: §a+REI$ ${"%.2f".format(profit)}") }, false)
                context.source.sendFeedback({ Text.literal("   §7Taxa: ${(inv.rate * 100).toInt()}% a.m. | Dias: ${inv.getDaysInvested()}") }, false)
            }
            
            val totalValue = account.investments.sumOf { it.getCurrentValue() }
            context.source.sendFeedback({ Text.literal("") }, false)
            context.source.sendFeedback({ Text.literal("§fTotal: §a+REI$ ${"%.2f".format(totalValue)}") }, false)
        }
        
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        return 1
    }
    
    private fun withdrawInvestment(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.playerOrThrow
        val index = IntegerArgumentType.getInteger(context, "index") - 1
        
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(player.uuid, player.name.string)
        
        if (index < 0 || index >= account.investments.size) {
            context.source.sendError(Text.literal("§cInvestimento não encontrado! Use /investimentos listar"))
            return 0
        }
        
        val investment = account.investments[index]
        val finalValue = investment.getCurrentValue()
        val profit = finalValue - investment.amount
        
        account.addBalance(finalValue)
        account.investments.removeAt(index)
        
        val transaction = Transaction(
            type = Transaction.TransactionType.INVESTMENT_RETURN,
            fromPlayer = "INVESTIMENTOS",
            toPlayer = account.playerName,
            amount = finalValue,
            description = "Resgate de investimento (Lucro: REI$ ${"%.2f".format(profit)})"
        )
        account.addTransaction(transaction)
        
        ScoreManager.updateScore(account)
        manager.saveData()
        
        context.source.sendFeedback({ Text.literal("§a§l✔ INVESTIMENTO RESGATADO!") }, false)
        context.source.sendFeedback({ Text.literal("§fValor resgatado: §2REI$ ${"%.2f".format(finalValue)}") }, false)
        context.source.sendFeedback({ Text.literal("§fLucro obtido: §a+REI$ ${"%.2f".format(profit)}") }, false)
        context.source.sendFeedback({ Text.literal("§fSaldo atual: §2REI$ ${"%.2f".format(account.balance)}") }, false)
        
        return 1
    }
    
    private fun checkScore(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.playerOrThrow
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(player.uuid, player.name.string)
        
        ScoreManager.updateScore(account)
        manager.saveData()
        
        val rating = ScoreManager.getScoreRating(account.score)
        
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        context.source.sendFeedback({ Text.literal("§a§lSCORE DE CRÉDITO") }, false)
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        context.source.sendFeedback({ Text.literal("§fScore: §f${account.score} §7(${rating}§7)") }, false)
        context.source.sendFeedback({ Text.literal("") }, false)
        context.source.sendFeedback({ Text.literal("§7Seu score afeta:") }, false)
        context.source.sendFeedback({ Text.literal("§7• Limite de empréstimo") }, false)
        context.source.sendFeedback({ Text.literal("§7• Taxa de juros") }, false)
        context.source.sendFeedback({ Text.literal("§7• Aprovação de crédito") }, false)
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        
        return 1
    }
    
    private fun showStatement(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.playerOrThrow
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(player.uuid, player.name.string)
        
        val history = account.transactionHistory
        
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        context.source.sendFeedback({ Text.literal("§a§lEXTRATO BANCÁRIO") }, false)
        context.source.sendFeedback({ Text.literal("§fSaldo atual: §2REI$ ${"%.2f".format(account.balance)}") }, false)
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        
        if (history.isEmpty()) {
            context.source.sendFeedback({ Text.literal("§7Nenhuma transação registrada.") }, false)
        } else {
            val limit = minOf(10, history.size)
            context.source.sendFeedback({ Text.literal("§fÚltimas $limit transações:") }, false)
            
            for (i in history.size - 1 downTo maxOf(0, history.size - 10)) {
                val transaction = history[i]
                val color = when (transaction.type) {
                    Transaction.TransactionType.PIX_RECEIVE,
                    Transaction.TransactionType.ADMIN_ADD,
                    Transaction.TransactionType.LOAN_TAKEN,
                    Transaction.TransactionType.INVESTMENT_RETURN -> "§a"
                    else -> "§c"
                }
                val sign = when (transaction.type) {
                    Transaction.TransactionType.PIX_RECEIVE,
                    Transaction.TransactionType.ADMIN_ADD,
                    Transaction.TransactionType.LOAN_TAKEN,
                    Transaction.TransactionType.INVESTMENT_RETURN -> "+"
                    else -> "-"
                }
                
                context.source.sendFeedback({ 
                    Text.literal("§7${transaction.getFormattedTimestamp()} §f$sign ${color}REI$ ${"%.2f".format(transaction.amount)}") 
                }, false)
                context.source.sendFeedback({ Text.literal("  §7${transaction.description}") }, false)
            }
        }
        
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        return 1
    }
    
    private fun addMoney(context: CommandContext<ServerCommandSource>): Int {
        val target = EntityArgumentType.getPlayer(context, "player")
        val amount = DoubleArgumentType.getDouble(context, "amount")
        
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(target.uuid, target.name.string)
        
        account.addBalance(amount)
        
        val transaction = Transaction(
            type = Transaction.TransactionType.ADMIN_ADD,
            fromPlayer = "ADMIN",
            toPlayer = account.playerName,
            amount = amount,
            description = "Adicionado por administrador"
        )
        account.addTransaction(transaction)
        
        manager.saveData()
        
        context.source.sendFeedback({ Text.literal(
            "§aAdicionado REI$ ${"%.2f".format(amount)} para ${target.name.string}"
        ) }, true)
        
        target.sendMessage(Text.literal("§aVocê recebeu REI$ ${"%.2f".format(amount)} de um administrador!"))
        
        return 1
    }
    
    private fun removeMoney(context: CommandContext<ServerCommandSource>): Int {
        val target = EntityArgumentType.getPlayer(context, "player")
        val amount = DoubleArgumentType.getDouble(context, "amount")
        
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(target.uuid, target.name.string)
        
        if (account.balance < amount) {
            context.source.sendError(Text.literal("§cO jogador não possui saldo suficiente!"))
            return 0
        }
        
        account.removeBalance(amount)
        
        val transaction = Transaction(
            type = Transaction.TransactionType.ADMIN_REMOVE,
            fromPlayer = account.playerName,
            toPlayer = "ADMIN",
            amount = amount,
            description = "Removido por administrador"
        )
        account.addTransaction(transaction)
        
        manager.saveData()
        
        context.source.sendFeedback({ Text.literal(
            "§aRemovido REI$ ${"%.2f".format(amount)} de ${target.name.string}"
        ) }, true)
        
        target.sendMessage(Text.literal("§cREI$ ${"%.2f".format(amount)} foram removidos da sua conta por um administrador!"))
        
        return 1
    }
    
    private fun setMoney(context: CommandContext<ServerCommandSource>): Int {
        val target = EntityArgumentType.getPlayer(context, "player")
        val amount = DoubleArgumentType.getDouble(context, "amount")
        
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(target.uuid, target.name.string)
        
        account.balance = amount
        manager.saveData()
        
        context.source.sendFeedback({ Text.literal(
            "§aSaldo de ${target.name.string} definido para REI$ ${"%.2f".format(amount)}"
        ) }, true)
        
        target.sendMessage(Text.literal("§eSeu saldo foi definido para REI$ ${"%.2f".format(amount)} por um administrador!"))
        
        return 1
    }
    
    private fun listAccounts(context: CommandContext<ServerCommandSource>): Int {
        val manager = BrazilEconomyMod.economyManager
        
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        context.source.sendFeedback({ Text.literal("§a§lCONTAS REGISTRADAS") }, false)
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        
        manager.getAllAccounts().forEach { account ->
            context.source.sendFeedback({ Text.literal(
                "§f${account.playerName}: §2REI$ ${"%.2f".format(account.balance)} " +
                if (account.hasCpf()) "§7(CPF: ${account.cpf})" else "§c(Sem CPF)"
            ) }, false)
        }
        
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        
        return 1
    }
    
    private fun viewPlayerAccount(context: CommandContext<ServerCommandSource>): Int {
        val target = EntityArgumentType.getPlayer(context, "player")
        val manager = BrazilEconomyMod.economyManager
        val account = manager.getOrCreateAccount(target.uuid, target.name.string)
        
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        context.source.sendFeedback({ Text.literal("§a§lCONTA DE ${target.name.string.uppercase()}") }, false)
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        context.source.sendFeedback({ Text.literal("§fSaldo: §2REI$ ${"%.2f".format(account.balance)}") }, false)
        context.source.sendFeedback({ Text.literal("§fCPF: §7${account.cpf ?: "Não registrado"}") }, false)
        context.source.sendFeedback({ Text.literal("§fScore: §f${account.score} §7(${ScoreManager.getScoreRating(account.score)}§7)") }, false)
        context.source.sendFeedback({ Text.literal("§fEmpréstimos: §c${account.loans.size} (REI$ ${"%.2f".format(account.loans.sumOf { it.amount })})") }, false)
        context.source.sendFeedback({ Text.literal("§fInvestimentos: §a${account.investments.size} (REI$ ${"%.2f".format(account.investments.sumOf { it.getCurrentValue() })})") }, false)
        context.source.sendFeedback({ Text.literal("§e§l═══════════════════════════") }, false)
        
        return 1
    }
}
